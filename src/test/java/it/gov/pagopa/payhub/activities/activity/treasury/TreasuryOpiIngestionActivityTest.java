package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpiParserService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TreasuryOpiIngestionActivityTest {

  @Mock
  private IngestionFlowFileDao ingestionFlowFileDao;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
  @Mock
  private TreasuryUnmarshallerService treasuryUnmarshallerService;
  @Mock
  private TreasuryOpiParserService treasuryOpiParserService;

  private TreasuryOpiIngestionActivity treasuryOpiIngestionActivity;

  @BeforeEach
  void setUp() {
    treasuryOpiIngestionActivity = new TreasuryOpiIngestionActivityImpl(
            ingestionFlowFileDao,
            ingestionFlowFileRetrieverService,
            treasuryOpiParserService
            );
  }

  private static final Long VALID_INGESTION_FLOW_ID = 1L;
  private static final Long NOT_FOUND_INGESTION_FLOW_ID = 8L;
  private static final Long INVALID_INGESTION_FLOW_ID = 9L;
  private static final IngestionFlowFileType VALID_INGESTION_FLOW_TYPE = IngestionFlowFileType.OPI;
  private static final IngestionFlowFileType INVALID_INGESTION_FLOW_TYPE = IngestionFlowFileType.PAYMENTS_REPORTING;
  private static final Path VALID_INGESTION_FLOW_PATH = Path.of("VALID_PATH");
  private static final String VALID_INGESTION_FLOW_FILE = "VALID_FILE";
  private static final String VALID_INGESTION_FLOW_IUF = "VALID_IUF";
  private static final Optional<IngestionFlowFileDTO> VALID_INGESTION_FLOW = Optional.of(IngestionFlowFileDTO.builder()
          .ingestionFlowFileId(VALID_INGESTION_FLOW_ID)
          .flowFileType(VALID_INGESTION_FLOW_TYPE)
          .filePath(VALID_INGESTION_FLOW_PATH.toString())
          .fileName(VALID_INGESTION_FLOW_FILE)
          .iuf(VALID_INGESTION_FLOW_IUF)
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




  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
    ingestionFlowFileDTO.setFlowFileType(IngestionFlowFileType.OPI);
    ingestionFlowFileDTO.setFilePath("/test/path");
    ingestionFlowFileDTO.setFileName("testFile.zip");

    Path mockPath = mock(Path.class);
    List<Path> paths = Collections.singletonList(mockPath);

    Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
    Mockito.when(ingestionFlowFileRetrieverService.retrieveAndUnzipFile(Path.of("/test/path"), "testFile.zip"))
            .thenReturn(paths);
    Mockito.when(treasuryOpiParserService.parseData(mockPath))
            .thenReturn(List.of());

    // When
    TreasuryIufResult result = treasuryOpiIngestionActivity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(0, result.getIufs().size());
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
  }

}
