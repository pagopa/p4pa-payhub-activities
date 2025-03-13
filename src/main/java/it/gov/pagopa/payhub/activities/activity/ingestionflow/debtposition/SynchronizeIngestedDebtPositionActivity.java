package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for the SynchronizeIngestedDebtPositionActivity.
 * Defines methods for synchronizing the debt positions ingested by a file.
 */
@ActivityInterface
public interface SynchronizeIngestedDebtPositionActivity {

    /**
     * Retrieves all debt positions ingested by a file and requests their synchronization
     * @param ingestionFlowFileId the unique identifier related to the file.
     * @return a string with possible synchronization errors
     */
    @ActivityMethod
    String synchronizeIngestedDebtPosition(Long ingestionFlowFileId);
}
