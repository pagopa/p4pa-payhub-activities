package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification.PaymentNotificationProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Lazy
@Component
public class PaymentNotificationIngestionActivityImpl extends BaseIngestionFlowFileActivity<PaymentNotificationIngestionFlowFileResult> implements PaymentNotificationIngestionActivity {

  private final CsvService csvService;
  private final PaymentNotificationProcessingService paymentNotificationProcessingService;

  protected PaymentNotificationIngestionActivityImpl(
      IngestionFlowFileService ingestionFlowFileService,
      IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
      FileArchiverService fileArchiverService,
      CsvService csvService,
      PaymentNotificationProcessingService paymentNotificationProcessingService) {
    super(ingestionFlowFileService, ingestionFlowFileRetrieverService,
        fileArchiverService);
    this.csvService = csvService;
    this.paymentNotificationProcessingService = paymentNotificationProcessingService;
  }

  @Override
  protected IngestionFlowFileTypeEnum getHandledIngestionFlowFileType() {
    return IngestionFlowFileTypeEnum.PAYMENT_NOTIFICATION;
  }

  @Override
  protected PaymentNotificationIngestionFlowFileResult handleRetrievedFiles(
      List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
    Path filePath = retrievedFiles.getFirst();
    Path workingDirectory = filePath.getParent();
    log.info("Processing file: {}", filePath);

    try {
      return csvService.readCsv(filePath,
          PaymentNotificationIngestionFlowFileDTO.class, (csvIterator, readerException) ->
              paymentNotificationProcessingService.processPaymentNotification(csvIterator, readerException,
                  ingestionFlowFileDTO, workingDirectory), ingestionFlowFileDTO.getFileVersion());
    } catch (Exception e) {
      log.error("Error processing file {}: {}", filePath, e.getMessage(), e);
      throw new InvalidIngestionFileException(String.format("Error processing file %s: %s", filePath, e.getMessage()));
    }

  }


}
