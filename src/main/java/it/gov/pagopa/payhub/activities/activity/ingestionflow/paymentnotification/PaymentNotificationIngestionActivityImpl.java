package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.BaseIngestionFlowFileActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification.PaymentNotificationProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class PaymentNotificationIngestionActivityImpl extends BaseIngestionFlowFileActivity<PaymentNotificationIngestionFlowFileActivityResult> implements PaymentNotificationIngestionActivity {

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
  protected PaymentNotificationIngestionFlowFileActivityResult handleRetrievedFiles(
      List<Path> retrievedFiles, IngestionFlowFile ingestionFlowFileDTO) {
    Path filePath = retrievedFiles.getFirst();
    Path workingDirectory = filePath.getParent();
    log.info("Processing file: {}", filePath);

    try {
      PaymentNotificationIngestionFlowFileResult result = csvService.readCsv(filePath,
          PaymentNotificationIngestionFlowFileDTO.class, csvIterator ->
              paymentNotificationProcessingService.processPaymentNotification(csvIterator,
                  ingestionFlowFileDTO, workingDirectory));

      List<String> iudList = result.getPaymentNotificationList().stream().map(
          PaymentNotificationDTO::getIud).toList();

      return new PaymentNotificationIngestionFlowFileActivityResult(
          iudList,
          result.getPaymentNotificationList().getFirst().getOrganizationId()
      );
    } catch (Exception e) {
      log.error("Error processing file {}: {}", filePath, e.getMessage());
      throw new InvalidIngestionFileException(String.format("Error processing file %s: %s", filePath, e.getMessage()));
    }

  }


}
