package it.gov.pagopa.payhub.activities.activity.ingestionflow;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
/**
 * Interface for the UpdateIngestionFlowStatusActivity.
 * Updates the status of a IngestionFlow record identified by the provided ID.
 */
@ActivityInterface
public interface UpdateIngestionFlowStatusActivity {

    /**
     * Updates the status of the IngestionFlow record corresponding to the given ID.
     *
     * @param id        the unique identifier of the record to update.
     * @param newStatus the new status to set.
     * @return true if the update was successful, false otherwise.
     */
    @ActivityMethod
    boolean updateStatus(Long id, String newStatus, String codError, String discardFileName);
}
