package it.gov.pagopa.payhub.activities.activity.ingestionflow.sendnotification;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification.SendNotificationProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class SendNotificationIngestionActivityTest {

  @Mock
  private CsvService csvServiceMock;
  @Mock
  private SendNotificationProcessingService sendNotificationProcessingService;
  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
  @Mock
  private FileArchiverService fileArchiverServiceMock;

  private SendNotificationIngestionActivity activity;

  @TempDir
  private Path workingDir;

  @BeforeEach
  void setUp() {
    activity = new SendNotificationIngestionActivityImpl(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        sendNotificationProcessingService
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        sendNotificationProcessingService
    );
  }

  @Test
  void handleRetrievedFilesSuccessfully() throws Exception {
    Long ingestionFlowFileId = 1L;
    Long organizationId = 10L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
    ingestionFlowFileDTO.setOrganizationId(organizationId);
    ingestionFlowFileDTO.setFilePathName(workingDir.toString());
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.SEND_NOTIFICATION);
    Iterator<SendNotificationIngestionFlowFileDTO> iterator = buildSendNotificationIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(SendNotificationIngestionFlowFileDTO.class), any(), isNull()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<SendNotificationIngestionFlowFileDTO>, List<CsvException>, SendNotificationIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(sendNotificationProcessingService.processSendNotifications(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenReturn(buildSendNotificationIngestionFlowFileResult());

    // When
    SendNotificationIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertEquals(
        buildSendNotificationIngestionFlowFileResult(),
        result);
    Mockito.verify(fileArchiverServiceMock, Mockito.times(1)).archive(ingestionFlowFileDTO);
    Assertions.assertFalse(filePath.toFile().exists());
  }


  @Test
  void givenValidIngestionFlowWhenExceptionThenThrowInvalidIngestionFileException() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    Long organizationId = 10L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
    ingestionFlowFileDTO.setFilePathName(workingDir.toString());
    ingestionFlowFileDTO.setOrganizationId(organizationId);
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.SEND_NOTIFICATION);
    Iterator<SendNotificationIngestionFlowFileDTO> iterator = buildSendNotificationIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(SendNotificationIngestionFlowFileDTO.class), any(), isNull()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<SendNotificationIngestionFlowFileDTO>, List<CsvException>, SendNotificationIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(sendNotificationProcessingService.processSendNotifications(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenThrow(new RestClientException("Error"));

    // When & Then
    assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
  }

  private SendNotificationIngestionFlowFileResult buildSendNotificationIngestionFlowFileResult() {
    return SendNotificationIngestionFlowFileResult.builder()
        .organizationId(1L)
        .processedRows(20L)
        .totalRows(30L)
        .discardedFileName("dicardedFileName")
        .errorDescription("errorDescription")
        .build();
  }

  private Iterator<SendNotificationIngestionFlowFileDTO> buildSendNotificationIngestionFlowFileDTO() {
    List<SendNotificationIngestionFlowFileDTO> sendNotificationIngestionFlowFileDTOS = List.of(
        SendNotificationIngestionFlowFileDTO.builder()
            .organizationId(10L)
            .paProtocolNumber("paProtocolNumber")
            .notificationFeePolicy("notificationFeePolicy")
            .physicalCommunicationType("physicalCommunicationType")
            .recipientType("recipientType")
            .taxId("taxId")
            .denomination("denomination")
            .address("denomination")
            .municipality("municipality")
            .build(),
        SendNotificationIngestionFlowFileDTO.builder()
            .organizationId(10L)
            .paProtocolNumber("paProtocolNumber2")
            .notificationFeePolicy("notificationFeePolicy")
            .physicalCommunicationType("physicalCommunicationType")
            .recipientType("recipientType")
            .taxId("taxId")
            .denomination("denomination")
            .address("denomination")
            .municipality("municipality")
            .build()
    );

    return sendNotificationIngestionFlowFileDTOS.iterator();
  }
}