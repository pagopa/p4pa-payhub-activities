package it.gov.pagopa.payhub.activities.activity.ingestionflow.massivedp;

import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.massivedp.InstallmentIngestionFlowFileResult;

/**
 * Interface for the InstallmentIngestionFlowFileActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
public interface InstallmentIngestionFlowFileActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link InstallmentIngestionFlowFileResult} containing the list of IUDs and InstallmentIDs.
     */
    @ActivityMethod
    InstallmentIngestionFlowFileResult processFile(Long ingestionFlowFileId);
}
