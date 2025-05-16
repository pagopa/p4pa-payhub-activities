package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.SyncIngestedDebtPositionDTO;

/**
 * Interface for the SynchronizeIngestedDebtPositionActivity.
 * Defines methods for synchronizing the debt positions ingested by a file.
 */
@ActivityInterface
public interface SynchronizeIngestedDebtPositionActivity {

    /**
     * Retrieves all debt positions ingested by a file and requests their synchronization
     * @param ingestionFlowFileId the unique identifier related to the file.
     * @return {@link SyncIngestedDebtPositionDTO} with possible synchronization errors and folderId generated from massive notice
     */
    @ActivityMethod
    SyncIngestedDebtPositionDTO synchronizeIngestedDebtPosition(Long ingestionFlowFileId);
}
