package it.gov.pagopa.payhub.activities.activity.fdr;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.fdr.FdRIngestionActivityResult;

/**
 * Interface for the FdRIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@ActivityInterface
public interface FdRIngestionActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowId the unique identifier related to the file to process.
     * @return {@link FdRIngestionActivityResult} containing the list of IUFs and status.
     */
    @ActivityMethod
    FdRIngestionActivityResult processFile(String ingestionFlowId);
}
