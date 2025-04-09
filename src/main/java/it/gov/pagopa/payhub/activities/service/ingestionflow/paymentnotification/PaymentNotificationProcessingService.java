package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification.PaymentNotificationMapper;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class PaymentNotificationProcessingService {

  private final PaymentNotificationMapper paymentNotificationMapper;
  private final PaymentNotificationErrorsArchiverService paymentNotificationErrorsArchiverService;

  public PaymentNotificationProcessingService(
      PaymentNotificationMapper paymentNotificationMapper,
      PaymentNotificationErrorsArchiverService paymentNotificationErrorsArchiverService) {
    this.paymentNotificationMapper = paymentNotificationMapper;
    this.paymentNotificationErrorsArchiverService = paymentNotificationErrorsArchiverService;
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
        paymentNotificationList.add(paymentNotificationMapper.map(paymentNotificationDTO, ingestionFlowFile));

        processedRows++;

      } catch (Exception e) {
        log.error("Error processing payment notice with iud {} and iuv {}: {}", paymentNotificationDTO.getIud(),paymentNotificationDTO.getIuv(),
            e.getMessage());
        PaymentNotificationErrorDTO error = new PaymentNotificationErrorDTO(
            ingestionFlowFile.getFileName(), paymentNotificationDTO.getIuv(),
            paymentNotificationDTO.getIud(), null, totalRows, "PROCESS_EXCEPTION", e.getMessage());
        errorList.add(error);
        log.info("Current error list size after handleProcessingError: {}", errorList.size());
      }
    }

    String errorsZipFileName = archiveErrorFiles(ingestionFlowFile, workingDirectory, errorList);
    return new PaymentNotificationIngestionFlowFileResult(totalRows, processedRows,
        errorsZipFileName != null ? "Some rows have failed" : null, errorsZipFileName,paymentNotificationList);
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

