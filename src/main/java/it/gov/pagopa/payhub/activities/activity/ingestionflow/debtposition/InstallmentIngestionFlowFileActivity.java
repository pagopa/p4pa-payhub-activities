package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileResult;

/**
 * Interface for the InstallmentIngestionFlowFileActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@ActivityInterface
public interface InstallmentIngestionFlowFileActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link InstallmentIngestionFlowFileResult} containing the number of total rows and the number of row correctly handled.
     */
    @ActivityMethod
    InstallmentIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}
