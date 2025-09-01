package it.gov.pagopa.payhub.activities.activity.ingestionflow.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileResult;

/**
 * Interface for the SendNotificationIngestionActivity.
 * Defines methods for processing assessments files based on an IngestionFlowFile ID.
 */
@ActivityInterface
public interface SendNotificationIngestionActivity {

  /**
   * Processes a processing assessments file based on the provided IngestionFlowFile ID.
   *
   * @param ingestionFlowFileId the unique identifier related to the file to process.
   * @return {@link SendNotificationIngestionFlowFileResult} containing the list of IUDs and organization Id.
   */
  @ActivityMethod(name = "ProcessSendNotificationFile")
  SendNotificationIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}
