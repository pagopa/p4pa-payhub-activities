package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;


import static org.junit.jupiter.api.Assertions.assertEquals;

import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationProcessingServiceTest {

  @Mock
  private Iterator<PaymentNotificationIngestionFlowFileDTO> iterator;

  @Mock
  private IngestionFlowFile ingestionFlowFile;

  @Mock
  private Path workingDirectory;

  private PaymentNotificationProcessingService service;

  @BeforeEach
  void setUp() {
    service = new PaymentNotificationProcessingService();
  }

  @Test
  void processPaymentNotificationWithEmptyIterator() {
    PaymentNotificationIngestionFlowFileActivityResult result = service.processPaymentNotification(iterator, ingestionFlowFile, workingDirectory);

    assertEquals(0L, result.getOrganizationId());
    assertEquals(Collections.emptyList(), result.getIudList());
  }
}