package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dao.FlussoTesoreriaPIIDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreasuryOpiParserServiceTest {

    private TreasuryUnmarshallerService treasuryUnmarshallerService;
    private TreasuryMapperService treasuryMapperService;
    private FlussoTesoreriaPIIDao flussoTesoreriaPIIDao;
    private TreasuryValidatorService treasuryValidatorService;
    private TreasuryDao treasuryDao;
    private TreasuryOpiParserService treasuryOpiParserService;

    @BeforeEach
    void setUp() {
        treasuryUnmarshallerService = mock(TreasuryUnmarshallerService.class);
        treasuryMapperService = mock(TreasuryMapperService.class);
        flussoTesoreriaPIIDao = mock(FlussoTesoreriaPIIDao.class);
        treasuryValidatorService = mock(TreasuryValidatorService.class);
        treasuryDao = mock(TreasuryDao.class);
        treasuryOpiParserService = new TreasuryOpiParserService(
                treasuryUnmarshallerService,
                treasuryMapperService,
                flussoTesoreriaPIIDao,
                treasuryValidatorService,
                treasuryDao
        );
    }

    @Test
    void testParseData_whenValidOpi161File_thenProcessSuccessfully() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);
        IngestionFlowFileDTO ingestionFlowFileDTO = mock(IngestionFlowFileDTO.class);

        FlussoGiornaleDiCassa flussoGiornaleDiCassa = new FlussoGiornaleDiCassa();
        flussoGiornaleDiCassa.getPagineTotali().add(2);

        when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenReturn(flussoGiornaleDiCassa);
        when(treasuryValidatorService.validatePageSize(any(), eq(flussoGiornaleDiCassa), eq(1), eq(TreasuryValidatorService.V_161)))
                .thenReturn(true);

        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> treasuryDtoMap = Map.of(
                TreasuryMapperService.INSERT, List.of(Pair.of(new TreasuryDTO(), new FlussoTesoreriaPIIDTO()))
        );
        when(treasuryMapperService.apply(flussoGiornaleDiCassa, ingestionFlowFileDTO)).thenReturn(treasuryDtoMap);

        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(flussoTesoreriaPIIDao, times(1)).insert(any(FlussoTesoreriaPIIDTO.class));
        verify(treasuryDao, times(1)).insert(any(TreasuryDTO.class));
    }

    @Test
    void testParseData_whenPageSizeInvalid_thenThrowsException() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        var flussoGiornaleDiCassa161 = mock(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);
        when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenReturn(flussoGiornaleDiCassa161);
        when(treasuryValidatorService.validatePageSize(null, flussoGiornaleDiCassa161, 1, TreasuryValidatorService.V_161))
                .thenReturn(false);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () ->
                treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1));
        assertEquals("invalid total page number for ingestionFlowFile with name null version v161", exception.getMessage());
    }

    @Test
    void testParseData_whenInvalidFile_thenThrowsException() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenThrow(new RuntimeException("Error parsing OPI 1.6.1"));
        when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenThrow(new RuntimeException("Error parsing OPI 1.4"));

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        // When & Then
        assertThrows(TreasuryOpiInvalidFileException.class, () -> treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1));
    }
}
