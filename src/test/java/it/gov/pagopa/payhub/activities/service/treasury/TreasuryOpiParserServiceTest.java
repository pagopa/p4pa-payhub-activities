package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dao.FlussoTesoreriaPIIDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


class TreasuryOpiParserServiceTest {

    private TreasuryUnmarshallerService treasuryUnmarshallerService;
    private TreasuryMapperService treasuryMapperService;
    private FlussoTesoreriaPIIDao flussoTesoreriaPIIDao;
    private TreasuryDao treasuryDao;
    private TreasuryOpiParserService treasuryOpiParserService;

    @BeforeEach
    void setUp() {
        treasuryUnmarshallerService = Mockito.mock(TreasuryUnmarshallerService.class);
        treasuryMapperService = Mockito.mock(TreasuryMapperService.class);
        flussoTesoreriaPIIDao = Mockito.mock(FlussoTesoreriaPIIDao.class);
        treasuryDao = Mockito.mock(TreasuryDao.class);
        treasuryOpiParserService = new TreasuryOpiParserService(
                treasuryUnmarshallerService,
                treasuryMapperService,
                flussoTesoreriaPIIDao,
                treasuryDao
        );
    }

    @Test
    void testParseData_whenValidOpi161File_thenProcessSuccessfully() {
        // Given
        Path filePath = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(filePath.toFile()).thenReturn(file);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        var flussoGiornaleDiCassa161 = Mockito.mock(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);
        Mockito.when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenReturn(flussoGiornaleDiCassa161);

        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> treasuryDtoMap = Map.of(
                TreasuryMapperService.INSERT, List.of(Pair.of(new TreasuryDTO(), new FlussoTesoreriaPIIDTO()))
        );
        Mockito.when(treasuryMapperService.apply(flussoGiornaleDiCassa161, ingestionFlowFileDTO)).thenReturn(treasuryDtoMap);

        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO);

        // Then
       Assertions.assertNotNull(result);
       Assertions.assertTrue(result.isSuccess());
        Mockito.verify(flussoTesoreriaPIIDao, Mockito.times(1)).insert(Mockito.any(FlussoTesoreriaPIIDTO.class));
        Mockito.verify(treasuryDao, Mockito.times(1)).insert(Mockito.any(TreasuryDTO.class));
    }

    @Test
    void testParseData_whenValidOpi14File_thenProcessSuccessfully() {
        // Given
        Path filePath = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(filePath.toFile()).thenReturn(file);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        var flussoGiornaleDiCassa14 = Mockito.mock(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa.class);
        Mockito.when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenThrow(new RuntimeException("Error parsing OPI 1.6.1"));
        Mockito.when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenReturn(flussoGiornaleDiCassa14);

        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> treasuryDtoMap = Map.of(
                TreasuryMapperService.INSERT, List.of(Pair.of(new TreasuryDTO(), new FlussoTesoreriaPIIDTO()))
        );
        Mockito.when(treasuryMapperService.apply(flussoGiornaleDiCassa14, ingestionFlowFileDTO)).thenReturn(treasuryDtoMap);

        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO);

        // Then
       Assertions.assertNotNull(result);
       Assertions.assertTrue(result.isSuccess());
        Mockito.verify(flussoTesoreriaPIIDao, Mockito.times(1)).insert(Mockito.any(FlussoTesoreriaPIIDTO.class));
        Mockito.verify(treasuryDao, Mockito.times(1)).insert(Mockito.any(TreasuryDTO.class));
    }

    @Test
    void testParseData_whenInvalidFile_thenThrowException() {
        // Given
        Path filePath = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(filePath.toFile()).thenReturn(file);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        Mockito.when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenThrow(new RuntimeException("Error parsing OPI 1.6.1"));
        Mockito.when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenThrow(new RuntimeException("Error parsing OPI 1.4"));

        // When & Then
       Assertions.assertThrows(TreasuryOpiInvalidFileException.class, () ->
                treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO));
    }
}
