package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIngestionResulDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpi14MapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.ObjectFactory;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TreasuryOpiIngestionActivityTest {

  @Mock
  private IngestionFlowFileDao ingestionFlowFileDao;
  @Mock
  private TreasuryDao treasuryDao;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
  @Mock
  private TreasuryUnmarshallerService treasuryUnmarshallerService;
  @Mock
  private TreasuryOpi14MapperService treasuryOpi14MapperService;

  private TreasuryOpiIngestionActivity treasuryOpiIngestionActivity;

  @BeforeEach
  void setUp() {
    treasuryOpiIngestionActivity = new TreasuryOpiIngestionActivityImpl(VALID_INGESTION_FLOW_TYPE,
      ingestionFlowFileDao,
      treasuryDao,
      ingestionFlowFileRetrieverService,
      treasuryUnmarshallerService,
      treasuryOpi14MapperService);
  }

  private final static Long VALID_INGESTION_FLOW_ID = 1L;
  private final static Long NOT_FOUND_INGESTION_FLOW_ID = 8L;
  private final static Long INVALID_INGESTION_FLOW_ID = 9L;
  private final static String VALID_INGESTION_FLOW_TYPE = "VALID_TYPE";
  private final static String INVALID_INGESTION_FLOW_TYPE = "INVALID_TYPE";
  private final static Path VALID_INGESTION_FLOW_PATH = Path.of("VALID_PATH");
  private final static String VALID_INGESTION_FLOW_FILE = "VALID_FILE";
  private final static String VALID_INGESTION_FLOW_IUF = "VALID_IUF";
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
  private final static List<FlussoGiornaleDiCassa> VALID_FLUSSO_OPI14_LIST = List.of(
    new ObjectFactory().createFlussoGiornaleDiCassa(),
    new ObjectFactory().createFlussoGiornaleDiCassa());
  private final static List<String> VALID_IUV_LIST = List.of(
    "VALID_IUV_1",
    "VALID_IUV_2");
  private final static List<TreasuryDTO> VALID_TREASURY_LIST = List.of(
    TreasuryDTO.builder()
      .codIdUnivocoFlusso(VALID_INGESTION_FLOW_IUF)
      .codIdUnivocoVersamento(VALID_IUV_LIST.get(0))
      .build(),
    TreasuryDTO.builder()
      .codIdUnivocoFlusso(VALID_INGESTION_FLOW_IUF)
      .codIdUnivocoVersamento(VALID_IUV_LIST.get(1))
      .build());

  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    //given
    Mockito.when(ingestionFlowFileDao.findById(VALID_INGESTION_FLOW_ID)).thenReturn(VALID_INGESTION_FLOW);
    Mockito.when(ingestionFlowFileRetrieverService.retrieveAndUnzipFile(VALID_INGESTION_FLOW_PATH, VALID_INGESTION_FLOW_FILE)).thenReturn(VALID_FILE_PATH_LIST);
    for (int i = 0; i < VALID_FILE_PATH_LIST.size(); i++) {
      Mockito.when(treasuryUnmarshallerService.unmarshalOpi14(VALID_FILE_PATH_LIST.get(i).toFile())).thenReturn(VALID_FLUSSO_OPI14_LIST.get(i));
      Mockito.when(treasuryOpi14MapperService.apply(VALID_FLUSSO_OPI14_LIST.get(i), VALID_INGESTION_FLOW.orElseThrow())).thenReturn(VALID_TREASURY_LIST.get(i));
      Mockito.when(treasuryDao.insert(VALID_TREASURY_LIST.get(i))).thenReturn(1L);
    }

    //when
    TreasuryIngestionResulDTO result = treasuryOpiIngestionActivity.processFile(VALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertNotNull(result.getIufIuvs());
    Assertions.assertEquals(2, result.getIufIuvs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(VALID_INGESTION_FLOW_ID);
    Mockito.verify(ingestionFlowFileRetrieverService, Mockito.times(1)).retrieveAndUnzipFile(VALID_INGESTION_FLOW_PATH, VALID_INGESTION_FLOW_FILE);
    for (int i = 0; i < VALID_FILE_PATH_LIST.size(); i++) {
      Assertions.assertEquals(VALID_INGESTION_FLOW_IUF, result.getIufIuvs().get(i).getIuf());
      Assertions.assertEquals(VALID_IUV_LIST.get(i), result.getIufIuvs().get(i).getIuv());
      Mockito.verify(treasuryUnmarshallerService, Mockito.times(1)).unmarshalOpi14(VALID_FILE_PATH_LIST.get(i).toFile());
      Mockito.verify(treasuryOpi14MapperService, Mockito.times(1)).apply(VALID_FLUSSO_OPI14_LIST.get(i), VALID_INGESTION_FLOW.orElseThrow());
      Mockito.verify(treasuryDao, Mockito.times(1)).insert(VALID_TREASURY_LIST.get(i));
    }
  }

  @Test
  void givenIngestionFlowNotFoundWhenProcessFileThenNoSuccess() {
    //given
    Mockito.when(ingestionFlowFileDao.findById(NOT_FOUND_INGESTION_FLOW_ID)).thenReturn(Optional.empty());

    //when
    TreasuryIngestionResulDTO result = treasuryOpiIngestionActivity.processFile(NOT_FOUND_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufIuvs());
    Assertions.assertEquals(0, result.getIufIuvs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(NOT_FOUND_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryDao, ingestionFlowFileRetrieverService, treasuryUnmarshallerService, treasuryOpi14MapperService);
  }

  @Test
  void givenIngestionFlowTypeInvalidWhenProcessFileThenNoSuccess() {
    //given
    Mockito.when(ingestionFlowFileDao.findById(INVALID_INGESTION_FLOW_ID)).thenReturn(INVALID_INGESTION_FLOW);

    //when
    TreasuryIngestionResulDTO result = treasuryOpiIngestionActivity.processFile(INVALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufIuvs());
    Assertions.assertEquals(0, result.getIufIuvs().size());
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).findById(INVALID_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryDao, ingestionFlowFileRetrieverService, treasuryUnmarshallerService, treasuryOpi14MapperService);
  }

}
