package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreasuryOpiIngestionActivityTest {

  @Mock
  private IngestionFlowFileDao ingestionFlowFileDao;
  @Mock
  private TreasuryDao treasuryDao;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
  @Mock
  private TreasuryUnmarshallerService treasuryUnmarshallerService;
  @Mock
  private TreasuryMapperService treasuryMapperService;
  @Mock
  private TreasuryOpiParserService treasuryOpiParserService;
  @Mock
  private TreasuryOpiIngestionActivity treasuryOpiIngestionActivity;


  @BeforeEach
  void setUp() {
    treasuryOpiIngestionActivity = new TreasuryOpiIngestionActivityImpl(
            ingestionFlowFileDao,
            ingestionFlowFileRetrieverService,
            treasuryOpiParserService
    );
  }

  private static final Long NOT_FOUND_INGESTION_FLOW_ID = 8L;
  private static final Long INVALID_INGESTION_FLOW_ID = 9L;
  private static final IngestionFlowFileType INVALID_INGESTION_FLOW_TYPE = IngestionFlowFileType.PAYMENTS_REPORTING;
  private static final Optional<IngestionFlowFileDTO> INVALID_INGESTION_FLOW = Optional.of(IngestionFlowFileDTO.builder()
          .ingestionFlowFileId(INVALID_INGESTION_FLOW_ID)
          .flowFileType(INVALID_INGESTION_FLOW_TYPE)
          .build());

  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
    ingestionFlowFileDTO.setFlowFileType(IngestionFlowFileType.OPI);
    ingestionFlowFileDTO.setFilePathName("/test/path");
    ingestionFlowFileDTO.setFileName("testFile.zip");

    Path mockPath = mock(Path.class);
    List<Path> paths = List.of(mockPath);

    Mockito.when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
    Mockito.when(ingestionFlowFileRetrieverService.retrieveAndUnzipFile(Path.of("/test/path"), "testFile.zip"))
            .thenReturn(paths);
    Mockito.when(treasuryOpiParserService.parseData(mockPath, ingestionFlowFileDTO,  paths.size()))
            .thenReturn(new TreasuryIufResult(Collections.singletonList("IUF123"), true));

    // When
    TreasuryIufResult result = treasuryOpiIngestionActivity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(1, result.getIufs().size());
    Assertions.assertEquals("IUF123", result.getIufs().get(0));
  }


  @Test
  void givenIngestionFlowNotFoundWhenProcessFileThenNoSuccess() {
    //given
    when(ingestionFlowFileDao.findById(NOT_FOUND_INGESTION_FLOW_ID)).thenReturn(Optional.empty());

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
    when(ingestionFlowFileDao.findById(INVALID_INGESTION_FLOW_ID)).thenReturn(INVALID_INGESTION_FLOW);

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
