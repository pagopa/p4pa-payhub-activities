package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryMapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpiParserService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryOpiIngestionActivityTest {

  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;
  @Mock
  private TreasuryService treasuryServiceMock;
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
            ingestionFlowFileServiceMock,
            ingestionFlowFileRetrieverServiceMock,
            treasuryOpiParserServiceMock,
            ingestionFlowFileArchiverServiceMock,
            treasuryErrorsArchiverServiceMock
    );
  }

  private static final Long NOT_FOUND_INGESTION_FLOW_ID = 8L;
  private static final Long INVALID_INGESTION_FLOW_ID = 9L;
  private static final IngestionFlowFile.FlowFileTypeEnum INVALID_INGESTION_FLOW_TYPE = IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING;
  private static final IngestionFlowFile.FlowFileTypeEnum VALID_INGESTION_FLOW_TYPE = IngestionFlowFile.FlowFileTypeEnum.TREASURY_OPI;
  private static final IngestionFlowFile INVALID_INGESTION_FLOW = IngestionFlowFile.builder()
          .ingestionFlowFileId(INVALID_INGESTION_FLOW_ID)
          .flowFileType(INVALID_INGESTION_FLOW_TYPE)
          .build();

  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFile.builder()
            .ingestionFlowFileId(ingestionFlowFileId)
            .flowFileType(VALID_INGESTION_FLOW_TYPE)
            .filePathName(workingDir.toString())
            .fileName("testFile.zip")
            .organizationId(0L)
            .build();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
            .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(treasuryOpiParserServiceMock.parseData(filePath, ingestionFlowFileDTO,  mockedListPath.size()))
            .thenReturn(Collections.singletonMap("IUF123", "treasury123"));

    Mockito.when(treasuryErrorsArchiverServiceMock.archiveErrorFiles(mockedListPath.getFirst().getParent(), ingestionFlowFileDTO))
            .thenReturn("DISCARDFILENAME");

    // When
    TreasuryIufIngestionFlowFileResult result = treasuryOpiIngestionActivityMock.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getIuf2TreasuryIdMap().size());
    Assertions.assertEquals("treasury123", result.getIuf2TreasuryIdMap().get("IUF123"));
    Assertions.assertEquals("DISCARDFILENAME", result.getDiscardedFileName());

    Mockito.verify(ingestionFlowFileArchiverServiceMock, Mockito.times(1))
            .archive(ingestionFlowFileDTO);
  }
  @Test
  void givenIngestionFlowNotFoundWhenProcessFileThenNoSuccess() {
    //given
    when(ingestionFlowFileServiceMock.findById(NOT_FOUND_INGESTION_FLOW_ID)).thenReturn(Optional.empty());

    //when
    TreasuryIufIngestionFlowFileResult result = treasuryOpiIngestionActivityMock.processFile(NOT_FOUND_INGESTION_FLOW_ID);

    //verify
    Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
    Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).findById(NOT_FOUND_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryServiceMock, ingestionFlowFileRetrieverServiceMock, treasuryUnmarshallerServiceMock, treasuryMapperServiceMock);
  }

  @Test
  void givenIngestionFlowTypeInvalidWhenProcessFileThenNoSuccess() {
    //given
    when(ingestionFlowFileServiceMock.findById(INVALID_INGESTION_FLOW_ID)).thenReturn(Optional.of(INVALID_INGESTION_FLOW));

    //when
    TreasuryIufIngestionFlowFileResult result = treasuryOpiIngestionActivityMock.processFile(INVALID_INGESTION_FLOW_ID);

    //verify
    Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
    Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).findById(INVALID_INGESTION_FLOW_ID);
    Mockito.verifyNoInteractions(treasuryServiceMock, ingestionFlowFileRetrieverServiceMock, treasuryUnmarshallerServiceMock, treasuryMapperServiceMock);
  }


  @Test
  void givenWrongTypeIngestionFlowFileWhenProcessFileThenFails() {
    // Given
    long ingestionFlowFileId = 123L;
    IngestionFlowFile mockFlowDTO = IngestionFlowFile.builder()
            .ingestionFlowFileId(ingestionFlowFileId)
            .flowFileType(INVALID_INGESTION_FLOW_TYPE)
            .build();

    when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
    // When
    TreasuryIufIngestionFlowFileResult result = treasuryOpiIngestionActivityMock.processFile(ingestionFlowFileId);

    // Then
  }

  @Test
  void givenIOExceptionWhenProcessFileThenFails() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFile.builder()
            .ingestionFlowFileId(ingestionFlowFileId)
            .flowFileType(VALID_INGESTION_FLOW_TYPE)
            .filePathName(workingDir.toString())
            .fileName("testFile.zip")
            .organizationId(0L)
            .build();
    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    TreasuryIufIngestionFlowFileResult expected =
            new TreasuryIufIngestionFlowFileResult(Collections.emptyMap(), null, "error occured", null);

    when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
            .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    doThrow(new RuntimeException("error occured")).when(treasuryErrorsArchiverServiceMock)
            .archiveErrorFiles(workingDir, ingestionFlowFileDTO);

    // When
    TreasuryIufIngestionFlowFileResult result = treasuryOpiIngestionActivityMock.processFile(ingestionFlowFileId);

    // Then
    assertEquals(expected, result);
  }
}