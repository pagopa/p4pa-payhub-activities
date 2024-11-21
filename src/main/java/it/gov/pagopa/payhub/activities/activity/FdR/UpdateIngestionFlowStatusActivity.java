package it.gov.pagopa.payhub.activities.activity.FdR;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for the UpdateIngestionFlowStatusActivity.
 * Updates the status of a database row identified by the provided ID.
 */
@ActivityInterface
public interface UpdateIngestionFlowStatusActivity {

    /**
     * Updates the status of the row corresponding to the given ID.
     *
     * @param id        the unique identifier of the row to update.
     * @param newStatus the new status to set.
     * @return true if the update was successful, false otherwise.
     */
    @ActivityMethod
    boolean updateStatus(String id, String newStatus);
}
