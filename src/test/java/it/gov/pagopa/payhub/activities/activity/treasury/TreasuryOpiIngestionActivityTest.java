package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.FlussoTesoreriaPIIDao;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.*;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.ObjectFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TreasuryOpiIngestionActivityTest {

  @Mock
  private IngestionFlowFileDao ingestionFlowFileDao;
  @Mock
  private TreasuryDao treasuryDao;
  @Mock
  private FlussoTesoreriaPIIDao flussoTesoreriaPIIDao;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
  @Mock
  private TreasuryUnmarshallerService treasuryUnmarshallerService;
  @Mock
  private TreasuryMapperService treasuryMapperService;
  private TreasuryOpiIngestionActivity treasuryOpiIngestionActivity;

  @BeforeEach
  void setUp() {
    treasuryOpiIngestionActivity = new TreasuryOpiIngestionActivityImpl(
            ingestionFlowFileDao,
            treasuryDao,
            flussoTesoreriaPIIDao,
            ingestionFlowFileRetrieverService,
            treasuryUnmarshallerService,
            treasuryMapperService
    );
  }

  private static final Long VALID_INGESTION_FLOW_ID = 1L;
  private static final Long NOT_FOUND_INGESTION_FLOW_ID = 8L;
  private static final Long INVALID_INGESTION_FLOW_ID = 9L;
  private static final IngestionFlowFileType VALID_INGESTION_FLOW_TYPE = IngestionFlowFileType.OPI;
  private static final IngestionFlowFileType INVALID_INGESTION_FLOW_TYPE = IngestionFlowFileType.PAYMENTS_REPORTING;
  private static final Path VALID_INGESTION_FLOW_PATH = Path.of("VALID_PATH");
  private static final String VALID_INGESTION_FLOW_FILE = "VALID_FILE";
  private static final List<String> VALID_INGESTION_FLOW_IUF = List.of("VALID_IUF_1", "VALID_IUF_2");
  private static final String PII_COGNOME = "PII_COGNOME";
  private static final String PII_DE_CAUSALE = "PII_DE_CAUSALE";
  private static final String KEY_MAP = "INSERT";
  private static final Optional<IngestionFlowFileDTO> VALID_INGESTION_FLOW = Optional.of(IngestionFlowFileDTO.builder()
          .ingestionFlowFileId(VALID_INGESTION_FLOW_ID)
          .flowFileType(VALID_INGESTION_FLOW_TYPE)
          .filePath(VALID_INGESTION_FLOW_PATH.toString())
          .fileName(VALID_INGESTION_FLOW_FILE)
          .iuf(VALID_INGESTION_FLOW_IUF.get(0))
          .build());
  private static final Optional<IngestionFlowFileDTO> INVALID_INGESTION_FLOW = Optional.of(IngestionFlowFileDTO.builder()
          .ingestionFlowFileId(INVALID_INGESTION_FLOW_ID)
          .flowFileType(INVALID_INGESTION_FLOW_TYPE)
          .build());
  private static final List<Path> VALID_FILE_PATH_LIST = List.of(
          Path.of("VALID_PATH_FILE_1"),
          Path.of("VALID_PATH_FILE_2")
  );
  private static final List<FlussoGiornaleDiCassa> VALID_FLUSSO_OPI14_LIST = List.of(
          new ObjectFactory().createFlussoGiornaleDiCassa(),
          new ObjectFactory().createFlussoGiornaleDiCassa());
  private static final List<String> VALID_IUV_LIST = List.of(
          "VALID_IUV_1",
          "VALID_IUV_2");
  private static final Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> VALID_TREASURY_MAP =
          Map.of(KEY_MAP, List.of(
                  Pair.of(
                          TreasuryDTO.builder()
                                  .flowIdentifierCode(VALID_INGESTION_FLOW_IUF.get(0))
                                  .iuv(VALID_IUV_LIST.get(0))
                                  .build(),
                          FlussoTesoreriaPIIDTO.builder()
                                  .deCognome(PII_COGNOME)
                                  .deCausale(PII_DE_CAUSALE)
                                  .build()),
                  Pair.of(
                          TreasuryDTO.builder()
                                  .flowIdentifierCode(VALID_INGESTION_FLOW_IUF.get(1))
                                  .iuv(VALID_IUV_LIST.get(1))
                                  .build(),
                          FlussoTesoreriaPIIDTO.builder()
                                  .deCognome(PII_COGNOME)
                                  .deCausale(PII_DE_CAUSALE)
                                  .build())));
  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    //given
    Mockito.when(ingestionFlowFileDao.findById(VALID_INGESTION_FLOW_ID)).thenReturn(VALID_INGESTION_FLOW);
    Mockito.when(ingestionFlowFileRetrieverService.retrieveAndUnzipFile(VALID_INGESTION_FLOW_PATH, VALID_INGESTION_FLOW_FILE)).thenReturn(VALID_FILE_PATH_LIST);
    for (int i = 0; i < VALID_FILE_PATH_LIST.size(); i++) {
      Mockito.when(treasuryUnmarshallerService.unmarshalOpi14(VALID_FILE_PATH_LIST.get(i).toFile())).thenReturn(VALID_FLUSSO_OPI14_LIST.get(i));
      Mockito.when(treasuryMapperService.apply(VALID_FLUSSO_OPI14_LIST.get(i), VALID_INGESTION_FLOW.orElseThrow())).thenReturn(VALID_TREASURY_MAP);
      Mockito.when(treasuryDao.insert(VALID_TREASURY_MAP.get(KEY_MAP).get(i).getLeft())).thenReturn(1L);
    }

    //when
    TreasuryIufResult result = treasuryOpiIngestionActivity.processFile(VALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertNotNull(result.getIufs());
    Assertions.assertEquals(2, result.getIufs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(VALID_INGESTION_FLOW_ID);
    Mockito.verify(ingestionFlowFileRetrieverService, Mockito.times(1)).retrieveAndUnzipFile(VALID_INGESTION_FLOW_PATH, VALID_INGESTION_FLOW_FILE);
    for (int i = 0; i < VALID_FILE_PATH_LIST.size(); i++) {
      Assertions.assertEquals(VALID_INGESTION_FLOW_IUF, result.getIufs());
      Mockito.verify(treasuryUnmarshallerService, Mockito.times(1)).unmarshalOpi14(VALID_FILE_PATH_LIST.get(i).toFile());
      Mockito.verify(treasuryMapperService, Mockito.times(1)).apply(VALID_FLUSSO_OPI14_LIST.get(i), VALID_INGESTION_FLOW.orElseThrow());
      Mockito.verify(treasuryDao, Mockito.times(2)).insert(VALID_TREASURY_MAP.get(KEY_MAP).get(i).getLeft());
    }
  }

  @Test
  void givenIngestionFlowNotFoundWhenProcessFileThenNoSuccess() {
    //given
    Mockito.when(ingestionFlowFileDao.findById(NOT_FOUND_INGESTION_FLOW_ID)).thenReturn(Optional.empty());

    //when
    TreasuryIufResult result = treasuryOpiIngestionActivity.processFile(NOT_FOUND_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufs());
    Assertions.assertEquals(0, result.getIufs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(NOT_FOUND_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryDao, ingestionFlowFileRetrieverService, treasuryUnmarshallerService, treasuryMapperService);
  }

  @Test
  void givenIngestionFlowTypeInvalidWhenProcessFileThenNoSuccess() {
    //given
    Mockito.when(ingestionFlowFileDao.findById(INVALID_INGESTION_FLOW_ID)).thenReturn(INVALID_INGESTION_FLOW);

    //when
    TreasuryIufResult result = treasuryOpiIngestionActivity.processFile(INVALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufs());
    Assertions.assertEquals(0, result.getIufs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(INVALID_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryDao, ingestionFlowFileRetrieverService, treasuryUnmarshallerService, treasuryMapperService);
  }

}
