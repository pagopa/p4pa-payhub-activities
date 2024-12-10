package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIngestionResultDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TreasuryOpiIngestionActivityTest {

  @Mock
  private IngestionFlowFileDao ingestionFlowFileDao;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;

  private TreasuryOpiIngestionActivity treasuryOpiIngestionActivity;

  @BeforeEach
  void setUp() {
    treasuryOpiIngestionActivity = new TreasuryOpiIngestionActivityImpl(VALID_INGESTION_FLOW_TYPE,
            ingestionFlowFileDao,
            ingestionFlowFileRetrieverService);
  }

  private final static Long VALID_INGESTION_FLOW_ID = 1L;
  private final static Long NOT_FOUND_INGESTION_FLOW_ID = 8L;
  private final static Long INVALID_INGESTION_FLOW_ID = 9L;
  private final static String VALID_INGESTION_FLOW_TYPE = "VALID_TYPE";
  private final static String INVALID_INGESTION_FLOW_TYPE = "INVALID_TYPE";
  private final static Path VALID_INGESTION_FLOW_PATH = Path.of("VALID_PATH");
  private final static String VALID_INGESTION_FLOW_FILE = "VALID_FILE";
  private final static String VALID_INGESTION_FLOW_IUF = "VALID_IUF";
  private final static String PII_COGNOME = "PII_COGNOME";
  private final static String PII_DE_CAUSALE = "PII_DE_CAUSALE";
  private final static String KEY_MAP = "INSERT";
  private final static Optional<IngestionFlowFileDTO> VALID_INGESTION_FLOW = Optional.of(IngestionFlowFileDTO.builder()
          .ingestionFlowFileId(VALID_INGESTION_FLOW_ID)
          .flowFileType(VALID_INGESTION_FLOW_TYPE)
          .filePathName(VALID_INGESTION_FLOW_PATH.toString())
          .fileName(VALID_INGESTION_FLOW_FILE)
          .iuf(VALID_INGESTION_FLOW_IUF)
          .build());
  private final static Optional<IngestionFlowFileDTO> INVALID_INGESTION_FLOW = Optional.of(IngestionFlowFileDTO.builder()
          .ingestionFlowFileId(INVALID_INGESTION_FLOW_ID)
          .flowFileType(INVALID_INGESTION_FLOW_TYPE)
          .build());
  private final static List<Path> VALID_FILE_PATH_LIST = List.of(
          Path.of("VALID_PATH_FILE_1"),
          Path.of("VALID_PATH_FILE_2")
  );

  private final static List<String> VALID_IUV_LIST = List.of(
          "VALID_IUV_1",
          "VALID_IUV_2");

  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    //given
    Mockito.when(ingestionFlowFileDao.findById(VALID_INGESTION_FLOW_ID)).thenReturn(VALID_INGESTION_FLOW);
    Mockito.when(ingestionFlowFileRetrieverService.retrieveAndUnzipFile(VALID_INGESTION_FLOW_PATH, VALID_INGESTION_FLOW_FILE)).thenReturn(VALID_FILE_PATH_LIST);
    for (int i = 0; i < VALID_FILE_PATH_LIST.size(); i++) {
    }

    //when
    TreasuryIngestionResultDTO result = treasuryOpiIngestionActivity.processFile(VALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertNotNull(result.getIufIuvs());
    Assertions.assertEquals(result.getIufIuvs(), new ArrayList<>());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(VALID_INGESTION_FLOW_ID);
    Mockito.verify(ingestionFlowFileRetrieverService, Mockito.times(1)).retrieveAndUnzipFile(VALID_INGESTION_FLOW_PATH, VALID_INGESTION_FLOW_FILE);
  }

  @Test
  void givenIngestionFlowNotFoundWhenProcessFileThenNoSuccess() {
    //given
    Mockito.when(ingestionFlowFileDao.findById(NOT_FOUND_INGESTION_FLOW_ID)).thenReturn(Optional.empty());

    //when
    TreasuryIngestionResultDTO result = treasuryOpiIngestionActivity.processFile(NOT_FOUND_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufIuvs());
    Assertions.assertEquals(0, result.getIufIuvs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(NOT_FOUND_INGESTION_FLOW_ID);
  }

  @Test
  void givenIngestionFlowTypeInvalidWhenProcessFileThenNoSuccess() {
    //given
    Mockito.when(ingestionFlowFileDao.findById(INVALID_INGESTION_FLOW_ID)).thenReturn(INVALID_INGESTION_FLOW);

    //when
    TreasuryIngestionResultDTO result = treasuryOpiIngestionActivity.processFile(INVALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufIuvs());
    Assertions.assertEquals(0, result.getIufIuvs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(INVALID_INGESTION_FLOW_ID);
  }

}
