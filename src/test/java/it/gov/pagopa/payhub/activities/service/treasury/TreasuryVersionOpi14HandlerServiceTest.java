package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreasuryVersionOpi14HandlerServiceTest {

    private TreasuryMapperOpi14Service mapperService;
    private TreasuryValidatorOpi14Service validatorService;
    private TreasuryUnmarshallerService treasuryUnmarshallerService;
    private TreasuryVersionOpi14HandlerService handlerService;
    private IngestionFlowFileArchiverService ingestionFlowFileArchiverService;

    @BeforeEach
    void setUp() {
        mapperService = mock(TreasuryMapperOpi14Service.class);
        validatorService = mock(TreasuryValidatorOpi14Service.class);
        treasuryUnmarshallerService = mock(TreasuryUnmarshallerService.class);
        ingestionFlowFileArchiverService = mock(IngestionFlowFileArchiverService.class);
        handlerService = new TreasuryVersionOpi14HandlerService(mapperService, validatorService, treasuryUnmarshallerService, ingestionFlowFileArchiverService);
    }

    @Test
    void testUnmarshall_whenValidFile_thenReturnsFlussoGiornaleDiCassa() {
        // Given
        File file = mock(File.class);
        FlussoGiornaleDiCassa expectedFlusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenReturn(expectedFlusso);

        // When
        FlussoGiornaleDiCassa result = handlerService.unmarshall(file);

        // Then
        assertNotNull(result);
        assertEquals(expectedFlusso, result);
        verify(treasuryUnmarshallerService, times(1)).unmarshalOpi14(file);
    }

    @Test
    void testHandle_whenValidFile_thenProcessesSuccessfully() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenReturn(flusso);

        Map<TreasuryOperationEnum, List<TreasuryDTO>> expectedResult = Map.of();

        when(validatorService.validatePageSize(flusso, 1)).thenReturn(true);
        when(validatorService.validateData(flusso, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        when(mapperService.apply(flusso, ingestionFlowFileDTO)).thenReturn(expectedResult);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

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
        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenReturn(flusso);

        when(validatorService.validatePageSize(flusso, 1)).thenReturn(true);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mapperService, never()).apply(any(), any());
    }

    @Test
    void testHandle_whenUnmarshallFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenThrow(new RuntimeException("Unmarshall failed"));

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mapperService, never()).apply(any(), any());
    }

    @Test
    void testHandle_whenMapperFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenReturn(flusso);

        when(validatorService.validatePageSize(flusso, 1)).thenReturn(true);
        when(validatorService.validateData(flusso, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        when(mapperService.apply(flusso, ingestionFlowFileDTO)).thenThrow(new RuntimeException("Mapper failed"));

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
