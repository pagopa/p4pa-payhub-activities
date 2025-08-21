package it.gov.pagopa.payhub.activities.activity.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
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
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link SendNotificationIngestionActivity} for processing Send Notification ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of Send Notification files.
 */
@Slf4j
@Lazy
@Service
public class SendNotificationIngestionActivityImpl extends BaseIngestionFlowFileActivity<SendNotificationIngestionFlowFileResult> implements SendNotificationIngestionActivity {

  private final CsvService csvService;
  private final SendNotificationProcessingService sendNotificationProcessingService;

  /**
   * Constructor to initialize dependencies for Receipts ingestion.
   *
   * @param ingestionFlowFileService          DAO for accessing ingestion flow file records.
   * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
   * @param fileArchiverService               Service for archiving files.
   * @param csvService                        Service for handling CSV file operations.
   * @param sendNotificationProcessingService          Service for processing send notification.
   */
  public SendNotificationIngestionActivityImpl(IngestionFlowFileService ingestionFlowFileService,
      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
      FileArchiverService fileArchiverService,
      CsvService csvService,
      SendNotificationProcessingService sendNotificationProcessingService) {
    super(ingestionFlowFileService, ingestionFlowFileRetrieverService, fileArchiverService);
    this.csvService = csvService;
    this.sendNotificationProcessingService = sendNotificationProcessingService;
  }

  @Override
  protected IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
    return IngestionFlowFileTypeEnum.SEND_NOTIFICATION;
  }

  @Override
  protected SendNotificationIngestionFlowFileResult handleRetrievedFiles(List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
    Path filePath = retrievedFiles.getFirst();
    Path workingDirectory = filePath.getParent();
    log.info("Processing file: {}", filePath);

    try {
      return csvService.readCsv(filePath, SendNotificationIngestionFlowFileDTO.class, (csvIterator, readerExceptions) ->
          sendNotificationProcessingService.processSendNotifications(csvIterator, readerExceptions, ingestionFlowFileDTO, workingDirectory), ingestionFlowFileDTO.getFileVersion());
    } catch (Exception e) {
      log.error("Error processing file {} with version {}: {}", filePath, ingestionFlowFileDTO.getFileVersion(), e.getMessage(), e);
      throw new InvalidIngestionFileException(String.format("Error processing file %s with version %s: %s", filePath, ingestionFlowFileDTO.getFileVersion(), e.getMessage()));
    }
  }
}
