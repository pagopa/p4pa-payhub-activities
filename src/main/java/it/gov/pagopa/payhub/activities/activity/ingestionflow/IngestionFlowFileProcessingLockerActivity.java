package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface IngestionFlowFileProcessingLockerActivity {

    /**
     * Updates the status of the specified IngestionFlowFile to PROCESSING if no other record
     * with the same organizationId and flowFileType is already in PROCESSING status.
     *
     * @param ingestionFlowFileId the unique identifier of the IngestionFlowFile to update.
     */
    @ActivityMethod
    boolean acquireProcessingLock(Long ingestionFlowFileId);

}
