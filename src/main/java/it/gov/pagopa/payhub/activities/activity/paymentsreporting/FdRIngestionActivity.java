package it.gov.pagopa.payhub.activities.activity.paymentsreporting;
import it.gov.pagopa.payhub.activities.dto.reportingflow.FdRIngestionActivityResult;

/**
 * Interface for the FdRIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
public interface FdRIngestionActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowId the unique identifier related to the file to process.
     * @return {@link FdRIngestionActivityResult} containing the list of IUFs and status.
     */
    FdRIngestionActivityResult processFile(String ingestionFlowId);
}
