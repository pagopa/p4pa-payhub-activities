package it.gov.pagopa.payhub.activities.activity.treasury;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufIngestionFlowFileResult;

/**
 * Interface for the TreasuryOpiIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@ActivityInterface
public interface TreasuryOpiIngestionActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link TreasuryIufIngestionFlowFileResult} containing the list of IUFs and status.
     */
    @ActivityMethod
    TreasuryIufIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}