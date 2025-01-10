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
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
  private TreasuryMapperService<?> treasuryMapperServiceMock;
  @Mock
  private TreasuryOpiParserService treasuryOpiParserServiceMock;
  @Mock
  private TreasuryOpiIngestionActivity treasuryOpiIngestionActivityMock;
  @Mock
  private IngestionFlowFileArchiverService ingestionFlowFileArchiverServiceMock;
  @Mock
  private TreasuryErrorsArchiverService treasuryErrorsArchiverServiceMock;

  @TempDir
  Path workingDir;

  @BeforeEach
  void setUp() {
    treasuryOpiIngestionActivityMock = new TreasuryOpiIngestionActivityImpl(
            ingestionFlowFileDaoMock,
            ingestionFlowFileRetrieverServiceMock,
            treasuryOpiParserServiceMock,
            ingestionFlowFileArchiverServiceMock,
            treasuryErrorsArchiverServiceMock
    );
  }

  private static final Long NOT_FOUND_INGESTION_FLOW_ID = 8L;
  private static final Long INVALID_INGESTION_FLOW_ID = 9L;
  private static final IngestionFlowFileType INVALID_INGESTION_FLOW_TYPE = IngestionFlowFileType.PAYMENTS_REPORTING;
  private static final IngestionFlowFileDTO INVALID_INGESTION_FLOW = IngestionFlowFileDTO.builder()
          .ingestionFlowFileId(INVALID_INGESTION_FLOW_ID)
          .flowFileType(INVALID_INGESTION_FLOW_TYPE)
          .build();

  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileDTO.builder()
            .ingestionFlowFileId(ingestionFlowFileId)
            .flowFileType(IngestionFlowFileType.OPI)
            .filePathName(workingDir.toString())
            .fileName("testFile.zip")
            .build();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
            .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(treasuryOpiParserServiceMock.parseData(filePath, ingestionFlowFileDTO,  mockedListPath.size()))
            .thenReturn(new TreasuryIufResult(Collections.singletonList("IUF123"), true, null, null));

    Mockito.when(treasuryErrorsArchiverServiceMock.archiveErrorFiles(mockedListPath.getFirst().getParent(), ingestionFlowFileDTO))
            .thenReturn("DISCARDFILENAME");

    // When
    TreasuryIufResult result = treasuryOpiIngestionActivityMock.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertEquals(1, result.getIufs().size());
    Assertions.assertEquals("IUF123", result.getIufs().getFirst());
    Assertions.assertEquals("DISCARDFILENAME", result.getDiscardedFileName());

    Mockito.verify(ingestionFlowFileArchiverServiceMock, Mockito.times(1))
            .archive(ingestionFlowFileDTO);
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
    when(ingestionFlowFileDaoMock.findById(INVALID_INGESTION_FLOW_ID)).thenReturn(Optional.of(INVALID_INGESTION_FLOW));

    //when
    TreasuryIufResult result = treasuryOpiIngestionActivityMock.processFile(INVALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertNotNull(result.getIufs());
    Assertions.assertEquals(0, result.getIufs().size());
    Mockito.verify(ingestionFlowFileDaoMock, Mockito.times(1)).findById(INVALID_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryDaoMock, ingestionFlowFileRetrieverServiceMock, treasuryUnmarshallerServiceMock, treasuryMapperServiceMock);
  }


  @Test
  void givenWrongTypeIngestionFlowFileWhenProcessFileThenFails() {
    // Given
    long ingestionFlowFileId = 123L;
    IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
            .ingestionFlowFileId(ingestionFlowFileId)
            .flowFileType(INVALID_INGESTION_FLOW_TYPE)
            .build();

    when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
    // When
    TreasuryIufResult result = treasuryOpiIngestionActivityMock.processFile(ingestionFlowFileId);

    // Then
    assertFalse(result.isSuccess());
  }

  @Test
  void givenIOExceptionWhenProcessFileThenFails() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileDTO.builder()
            .ingestionFlowFileId(ingestionFlowFileId)
            .flowFileType(IngestionFlowFileType.OPI)
            .filePathName(workingDir.toString())
            .fileName("testFile.zip")
            .build();
    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    TreasuryIufResult expected =
            new TreasuryIufResult(Collections.emptyList(), false, "error occured", null);

    when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
            .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    doThrow(new IOException("error occured")).when(ingestionFlowFileArchiverServiceMock)
            .archive(ingestionFlowFileDTO);

    // When
    TreasuryIufResult result = treasuryOpiIngestionActivityMock.processFile(ingestionFlowFileId);

    // Then
    assertEquals(expected, result);
  }
}