package it.gov.pagopa.payhub.activities.activity.exportflow.email;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for SendEmailExportFileActivity.
 * Sends an email based on the status of a processed file identified by its ExportFile ID.
 */
@ActivityInterface
public interface SendEmailExportFileActivity {

    /**
     * Sends an email based on the process result of the given export file ID.
     *
     * @param exportFileId the unique identifier of the ExportFile record.
     * @param success true if the process succeeded, false otherwise.
     */
    @ActivityMethod
    void sendExportCompletedEmail(Long exportFileId, boolean success);
}
