package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification.PaymentNotificationMapper;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class PaymentNotificationProcessingService {

  private final PaymentNotificationMapper paymentNotificationMapper;
  private final PaymentNotificationErrorsArchiverService paymentNotificationErrorsArchiverService;
  private final PaymentNotificationService paymentNotificationService;

  public PaymentNotificationProcessingService(
      PaymentNotificationMapper paymentNotificationMapper,
      PaymentNotificationErrorsArchiverService paymentNotificationErrorsArchiverService,
      PaymentNotificationService paymentNotificationService) {
    this.paymentNotificationMapper = paymentNotificationMapper;
    this.paymentNotificationErrorsArchiverService = paymentNotificationErrorsArchiverService;
    this.paymentNotificationService = paymentNotificationService;
  }


  public PaymentNotificationIngestionFlowFileResult processPaymentNotification(
      Iterator<PaymentNotificationIngestionFlowFileDTO> iterator,
      IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
    List<PaymentNotificationErrorDTO> errorList = new ArrayList<>();
    List<PaymentNotificationDTO> paymentNotificationList = new ArrayList<>();
    long processedRows = 0;
    long totalRows = 0;

    while (iterator.hasNext()) {
      totalRows++;

      PaymentNotificationIngestionFlowFileDTO paymentNotificationDTO = iterator.next();

      try {

        PaymentNotificationDTO paymentNotificationCreated = paymentNotificationService.createPaymentNotification(
            paymentNotificationMapper.map(paymentNotificationDTO, ingestionFlowFile));

        paymentNotificationList.add(paymentNotificationCreated);

        processedRows++;

      } catch (Exception e) {
        log.error("Error processing payment notice with iud {} and iuv {}: {}",
            paymentNotificationDTO.getIud(), paymentNotificationDTO.getIuv(),
            e.getMessage());
        PaymentNotificationErrorDTO error = new PaymentNotificationErrorDTO(
            ingestionFlowFile.getFileName(), paymentNotificationDTO.getIuv(),
            paymentNotificationDTO.getIud(), null, totalRows, "PROCESS_EXCEPTION", e.getMessage());
        errorList.add(error);
        log.info("Current error list size after handleProcessingError: {}", errorList.size());
      }
    }

    String errorsZipFileName = archiveErrorFiles(ingestionFlowFile, workingDirectory, errorList);
    return PaymentNotificationIngestionFlowFileResult.builder()
            .iudList(paymentNotificationList.stream().map(
                    PaymentNotificationDTO::getIud).toList())
            .organizationId(ingestionFlowFile.getOrganizationId())
            .totalRows(totalRows)
            .processedRows(processedRows)
            .errorDescription(errorsZipFileName != null ? "Some rows have failed" : null)
            .discardedFileName(errorsZipFileName)
            .build();
  }

  private String archiveErrorFiles(IngestionFlowFile ingestionFlowFile, Path workingDirectory,
      List<PaymentNotificationErrorDTO> errorList) {
    if (errorList.isEmpty()) {
      log.info("No errors to archive for file: {}", ingestionFlowFile.getFileName());
      return null;
    }

    paymentNotificationErrorsArchiverService.writeErrors(workingDirectory, ingestionFlowFile,
        errorList);
    String errorsZipFileName = paymentNotificationErrorsArchiverService.archiveErrorFiles(
        workingDirectory, ingestionFlowFile);
    log.info("Error file archived at: {}", errorsZipFileName);

    return errorsZipFileName;
  }
}

