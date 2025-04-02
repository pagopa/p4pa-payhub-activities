package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class PaymentNotificationProcessingService {

  public PaymentNotificationIngestionFlowFileActivityResult processPaymentNotification(
      Iterator<PaymentNotificationIngestionFlowFileDTO> iterator,
      IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
      return new PaymentNotificationIngestionFlowFileActivityResult(Collections.emptyList(),0L);
  }


}

