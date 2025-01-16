package it.gov.pagopa.payhub.activities.service.treasury.opi14;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryVersionOpi14HandlerServiceTest {

    @Mock
    private TreasuryMapperOpi14Service mapperServiceMock;
    @Mock
    private TreasuryValidatorOpi14Service validatorServiceMock;
    @Mock
    private TreasuryUnmarshallerService treasuryUnmarshallerServiceMock;
    @Mock
    private TreasuryErrorsArchiverService treasuryErrorsArchiverServiceMock;
    @Mock
    private TreasuryService treasuryServiceMock;

    private TreasuryVersionOpi14HandlerService handlerService;

    @BeforeEach
    void setUp() {
        handlerService = new TreasuryVersionOpi14HandlerService(mapperServiceMock, validatorServiceMock, treasuryUnmarshallerServiceMock, treasuryErrorsArchiverServiceMock, treasuryServiceMock);
    }

    @Test
    void testUnmarshall_whenValidFile_thenReturnsFlussoGiornaleDiCassa() {
        // Given
        File file = mock(File.class);
        FlussoGiornaleDiCassa expectedFlusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerServiceMock.unmarshalOpi14(file)).thenReturn(expectedFlusso);

        // When
        FlussoGiornaleDiCassa result = handlerService.unmarshall(file);

        // Then
        assertNotNull(result);
        assertEquals(expectedFlusso, result);
        verify(treasuryUnmarshallerServiceMock, times(1)).unmarshalOpi14(file);
    }

    @Test
    void testHandle_whenValidFile_thenProcessesSuccessfully() {
        // Given
        File file = new File("build","testFile.csv");
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerServiceMock.unmarshalOpi14(file)).thenReturn(flusso);

        Map<TreasuryOperationEnum, List<Treasury>> expectedMap = Map.of();
        List<Treasury> expectedResult = List.of();

        ArrayList<TreasuryErrorDTO> errorListDto = new ArrayList<>();
        when(validatorServiceMock.validatePageSize(flusso, 1)).thenReturn(true);
        when(validatorServiceMock.validateData(flusso, ingestionFlowFileDTO.getFileName())).thenReturn(errorListDto);
        when(mapperServiceMock.apply(flusso, ingestionFlowFileDTO)).thenReturn(expectedMap);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);

    }

    @Test
    void testHandle_whenValidationFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerServiceMock.unmarshalOpi14(file)).thenReturn(flusso);

        when(validatorServiceMock.validatePageSize(flusso, 1)).thenReturn(false);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

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

        when(treasuryUnmarshallerServiceMock.unmarshalOpi14(file)).thenThrow(new RuntimeException("Unmarshall failed"));

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

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
        when(treasuryUnmarshallerServiceMock.unmarshalOpi14(file)).thenReturn(flusso);

        when(validatorServiceMock.validatePageSize(flusso, 1)).thenReturn(true);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
