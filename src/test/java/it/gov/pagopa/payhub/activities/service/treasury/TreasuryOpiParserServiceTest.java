package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TreasuryOpiParserServiceTest {

    private TreasuryUnmarshallerService treasuryUnmarshallerService;
    private TreasuryBaseOpiHandlerService treasuryBaseOpiHandlerService;
    private TreasuryDao treasuryDao;
    private TreasuryOpiParserService treasuryOpiParserService;

    @BeforeEach
    void setUp() {
        treasuryUnmarshallerService = mock(TreasuryUnmarshallerService.class);
        treasuryBaseOpiHandlerService = mock(TreasuryBaseOpiHandlerService.class);
        treasuryDao = mock(TreasuryDao.class);
        treasuryOpiParserService = new TreasuryOpiParserService(
                treasuryUnmarshallerService,
                treasuryBaseOpiHandlerService,
                treasuryDao
        );
    }

    @Test
    void testParseData_whenValidOpi161File_thenProcessesSuccessfully() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        var flusso161 = mock(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);
        when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenReturn(flusso161);

        TreasuryValidatorService<it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa> validator =
                mock(TreasuryValidatorService.class);
        when(treasuryBaseOpiHandlerService.getValidator(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class))
                .thenReturn(validator);
        when(validator.validatePageSize(flusso161, 1)).thenReturn(true);

        @SuppressWarnings("unchecked")
        TreasuryMapperService<it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa, Map<TreasuryOperationEnum, List<TreasuryDTO>>> mapper =
                mock(TreasuryMapperService.class);
        when(treasuryBaseOpiHandlerService.getMapper(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class))
                .thenReturn((TreasuryMapperService)mapper);

        Map<TreasuryOperationEnum, List<TreasuryDTO>> treasuryDtoMap = Map.of(
                TreasuryOperationEnum.INSERT, List.of(TreasuryDTO.builder()
                        .flowIdentifierCode("Flow123")
                        .build())
        );
        when(mapper.apply(flusso161, ingestionFlowFileDTO)).thenReturn(treasuryDtoMap);



        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getIufs().size());
        assertEquals("Flow123", result.getIufs().get(0));
        verify(treasuryDao, times(1)).insert(any(TreasuryDTO.class));
    }

    @Test
    void testParseData_whenInvalidOpi161File_thenFallsBackToOpi14() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenThrow(new RuntimeException("Error parsing OPI 1.6.1"));
        var flusso14 = mock(FlussoGiornaleDiCassa.class);
        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenReturn(flusso14);

        TreasuryValidatorService<FlussoGiornaleDiCassa> validator =
                mock(TreasuryValidatorService.class);
        when(treasuryBaseOpiHandlerService.getValidator(FlussoGiornaleDiCassa.class))
                .thenReturn(validator);
        when(validator.validatePageSize(flusso14, 1)).thenReturn(true);

        TreasuryMapperService<FlussoGiornaleDiCassa, Map<TreasuryOperationEnum, List<TreasuryDTO>>> mapper =
                mock(TreasuryMapperService.class);
        when(treasuryBaseOpiHandlerService.getMapper(FlussoGiornaleDiCassa.class))
                .thenReturn((TreasuryMapperService)mapper);

        Map<TreasuryOperationEnum, List<TreasuryDTO>> treasuryDtoMap = Map.of(
                TreasuryOperationEnum.INSERT, List.of(TreasuryDTO.builder()
                        .flowIdentifierCode("Flow456")
                        .build())
        );
        when(mapper.apply(flusso14, ingestionFlowFileDTO)).thenReturn(treasuryDtoMap);



        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getIufs().size());
        assertEquals("Flow456", result.getIufs().get(0));
        verify(treasuryDao, times(1)).insert(any(TreasuryDTO.class));
    }

    @Test
    void testParseData_whenBothParsersFail_thenThrowsException() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenThrow(new RuntimeException("Error parsing OPI 1.6.1"));
        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenThrow(new RuntimeException("Error parsing OPI 1.4"));

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        // When & Then
        assertThrows(TreasuryOpiInvalidFileException.class, () ->
                treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1));
    }

    @Test
    void testParseData_whenPageSizeInvalid_thenThrowsActivitiesException() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        var flusso161 = mock(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);
        when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenReturn(flusso161);

        TreasuryOpi161ValidatorService validator =
                mock(TreasuryOpi161ValidatorService.class);
        when(treasuryBaseOpiHandlerService.getValidator(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class))
                .thenReturn(validator);
        when(validator.validatePageSize(flusso161, 1)).thenReturn(false);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        // When & Then
        assertThrows(ActivitiesException.class, () ->
                treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1));
    }
}
