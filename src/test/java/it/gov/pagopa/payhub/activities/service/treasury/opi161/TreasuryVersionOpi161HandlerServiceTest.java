package it.gov.pagopa.payhub.activities.service.treasury.opi161;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryVersionOpi161HandlerServiceTest {

    @Mock
    private TreasuryMapperOpi161Service mapperServiceMock;
    @Mock
    private TreasuryValidatorOpi161Service validatorServiceMock;
    @Mock
    private TreasuryUnmarshallerService treasuryUnmarshallerServiceMock;
    @Mock
    private TreasuryErrorsArchiverService treasuryErrorsArchiverServiceMock;

    private TreasuryVersionOpi161HandlerService handlerService;

    @BeforeEach
    void setUp() {
        handlerService = new TreasuryVersionOpi161HandlerService(mapperServiceMock, validatorServiceMock, treasuryUnmarshallerServiceMock, treasuryErrorsArchiverServiceMock);
    }

    @Test
    void testUnmarshall_whenValidFile_thenReturnsFlussoGiornaleDiCassa() {
        // Given
        File file = mock(File.class);
        FlussoGiornaleDiCassa expectedFlusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerServiceMock.unmarshalOpi161(file)).thenReturn(expectedFlusso);

        // When
        FlussoGiornaleDiCassa result = handlerService.unmarshall(file);

        // Then
        assertNotNull(result);
        assertEquals(expectedFlusso, result);
        verify(treasuryUnmarshallerServiceMock, times(1)).unmarshalOpi161(file);
    }

    @Test
    void testHandle_whenValidFile_thenProcessesSuccessfully() {
        // Given
        File file = new File("build", "testFile.csv");
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerServiceMock.unmarshalOpi161(file)).thenReturn(flusso);

        Map<TreasuryOperationEnum, List<TreasuryDTO>> expectedResult = Map.of();

        ArrayList<TreasuryErrorDTO> errorDtoList = new ArrayList<>();
        when(validatorServiceMock.validatePageSize(flusso, 1)).thenReturn(true);
        when(validatorServiceMock.validateData(flusso, ingestionFlowFileDTO.getFileName())).thenReturn(errorDtoList);
        when(mapperServiceMock.apply(flusso, ingestionFlowFileDTO)).thenReturn(expectedResult);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);

        Mockito.verify(treasuryErrorsArchiverServiceMock)
                .writeErrors(eq(Path.of("build")), same(ingestionFlowFileDTO), same(errorDtoList));
    }

    @Test
    void testHandle_whenValidationFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerServiceMock.unmarshalOpi161(file)).thenReturn(flusso);

        when(validatorServiceMock.validatePageSize(flusso, 1)).thenReturn(false);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mapperServiceMock, never()).apply(any(), any());
    }

    @Test
    void testHandle_whenUnmarshallFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        when(treasuryUnmarshallerServiceMock.unmarshalOpi161(file)).thenThrow(new RuntimeException("Unmarshall failed"));

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mapperServiceMock, never()).apply(any(), any());
    }

    @Test
    void testHandle_whenMapperFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerServiceMock.unmarshalOpi161(file)).thenReturn(flusso);

        when(validatorServiceMock.validatePageSize(flusso, 1)).thenReturn(true);
        when(validatorServiceMock.validateData(flusso, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        when(mapperServiceMock.apply(flusso, ingestionFlowFileDTO)).thenThrow(new RuntimeException("Mapper failed"));

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
