package it.gov.pagopa.payhub.activities.activity.fdr;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for SendEmailIngestionFlowActivity.
 * Sends an email based on the status of a processed file identified by its ID.
 */
@ActivityInterface
public interface SendEmailIngestionFlowActivity {

    /**
     * Sends an email based on the process result of the given file ID.
     *
     * @param fileId       the unique identifier of the imported file.
     * @param success      true if the process succeeded, false otherwise.
     * @return true if the email was sent successfully, false otherwise.
     */
    @ActivityMethod
    boolean sendEmail(String fileId, boolean success);
}
