package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
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
  private IngestionFlowFileDao ingestionFlowFileDaoMock;
  @Mock
  private TreasuryDao treasuryDaoMock;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
  @Mock
  private TreasuryUnmarshallerService treasuryUnmarshallerServiceMock;
  @Mock
  private TreasuryMapperService treasuryMapperServiceMock;
  @Mock
  private TreasuryOpiParserService treasuryOpiParserServiceMock;
  @Mock
  private TreasuryOpiIngestionActivity treasuryOpiIngestionActivityMock;
  @Mock
  private IngestionFlowFileArchiverService ingestionFlowFileArchiverServiceMock;

  @BeforeEach
  void setUp() {
    treasuryOpiIngestionActivityMock = new TreasuryOpiIngestionActivityImpl(
            ingestionFlowFileDaoMock,
            ingestionFlowFileRetrieverServiceMock,
            treasuryOpiParserServiceMock,
            ingestionFlowFileArchiverServiceMock,
            "archiveDirectory",
            "errorDirectory"
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

    Mockito.when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
    Mockito.when(ingestionFlowFileRetrieverServiceMock.retrieveAndUnzipFile(Path.of("/test/path"), "testFile.zip"))
            .thenReturn(paths);
    Mockito.when(treasuryOpiParserServiceMock.parseData(mockPath, ingestionFlowFileDTO,  paths.size(),"errorDirectory"))
            .thenReturn(new TreasuryIufResult(Collections.singletonList("IUF123"), true, null));

    // When
    TreasuryIufResult result = treasuryOpiIngestionActivityMock.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(1, result.getIufs().size());
    Assertions.assertEquals("IUF123", result.getIufs().get(0));
  }


  @Test
  void givenIngestionFlowNotFoundWhenProcessFileThenNoSuccess() {
    //given
    when(ingestionFlowFileDaoMock.findById(NOT_FOUND_INGESTION_FLOW_ID)).thenReturn(Optional.empty());

    //when
    TreasuryIufResult result = treasuryOpiIngestionActivityMock.processFile(NOT_FOUND_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufs());
    Assertions.assertEquals(0, result.getIufs().size());
    Mockito.verify(ingestionFlowFileDaoMock, Mockito.times(1)).findById(NOT_FOUND_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryDaoMock, ingestionFlowFileRetrieverServiceMock, treasuryUnmarshallerServiceMock, treasuryMapperServiceMock);
  }

  @Test
  void givenIngestionFlowTypeInvalidWhenProcessFileThenNoSuccess() {
    //given
    when(ingestionFlowFileDaoMock.findById(INVALID_INGESTION_FLOW_ID)).thenReturn(INVALID_INGESTION_FLOW);

    //when
    TreasuryIufResult result = treasuryOpiIngestionActivityMock.processFile(INVALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufs());
    Assertions.assertEquals(0, result.getIufs().size());
    Mockito.verify(ingestionFlowFileDaoMock, Mockito.times(1)).findById(INVALID_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryDaoMock, ingestionFlowFileRetrieverServiceMock, treasuryUnmarshallerServiceMock, treasuryMapperServiceMock);
  }
}