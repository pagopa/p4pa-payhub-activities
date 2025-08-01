package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;

/**
 * Interface for the PaymentNotificationIngestionActivity.
 * Defines methods for processing payment notification files based on an IngestionFlowFile ID.
 */
@ActivityInterface
public interface PaymentNotificationIngestionActivity {

  /**
   * Processes a payment notification file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier related to the file to process.
   * @return {@link PaymentNotificationIngestionFlowFileResult} containing the list of IUDs and organization Id.
   */
  @ActivityMethod(name = "ProcessPaymentNotificationFile")
  PaymentNotificationIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}
