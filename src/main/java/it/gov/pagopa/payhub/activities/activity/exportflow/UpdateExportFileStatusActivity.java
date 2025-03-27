package it.gov.pagopa.payhub.activities.activity.exportflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;

/**
 * Updates the status of an ExportFile record identified by the provided ID.
 */
@ActivityInterface
public interface UpdateExportFileStatusActivity {

    /**
     * Updates the status of the IngestionFlow record corresponding to the given ID.
     *
     * @param id        the unique identifier of the record to update.
     * @param oldStatus the actual status to verify.
     * @param newStatus the new status to set.
     */
    @ActivityMethod
    void updateStatus(Long id, ExportFileStatus oldStatus, ExportFileStatus newStatus);
}
