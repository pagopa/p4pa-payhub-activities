package it.gov.pagopa.payhub.activities.activity.ingestionflow;

/**
 * Interface for SendEmailIngestionFlowActivity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
public interface SendEmailIngestionFlowActivity {
    /**
     * Sends an email based on the process result of the given file ingestionFlow ID.
     *
     * @param ingestionFlowFileId       the unique identifier of the IngestionFlow record related to the imported file.
     * @param success      true if the process succeeded, false otherwise.
     * @return true if the email was sent successfully, false otherwise.
     */
    boolean sendEmail(Long ingestionFlowFileId, boolean success);
}
