package it.gov.pagopa.payhub.activities.activity.utility;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
/**
 * Interface for SendEmailIngestionFlowActivity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
@ActivityInterface
public interface SendEmailIngestionFlowActivity {

    /**
     * Sends an email based on the process result of the given file ingestionFlow ID.
     *
     * @param ingestionFlowId       the unique identifier of the IngestionFlow record related to the imported file.
     * @param success      true if the process succeeded, false otherwise.
     * @return true if the email was sent successfully, false otherwise.
     */
    @ActivityMethod
    boolean sendEmail(Long ingestionFlowId, boolean success);
}
