package it.gov.pagopa.payhub.activities.activity.exportflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;

/**
 * Updates the status of an ExportFile record identified by the provided ID.
 */
@ActivityInterface
public interface UpdateExportFileStatusActivity {

    /**
     * Updates the status of the IngestionFlow record corresponding to the given ID.
     *
     * @param updateStatusRequest the DTO containing the fields to set.
     */
    @ActivityMethod
    void updateExportStatus(UpdateStatusRequest updateStatusRequest);
}
