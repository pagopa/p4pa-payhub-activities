package it.gov.pagopa.payhub.activities.activity.paymentsreporting;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;

/**
 * Interface for the PaymentsReportingIngestionFlowFileActivity.
 * Defines methods for processing payments reporting files based on an IngestionFlowFile ID.
 */
public interface PaymentsReportingIngestionFlowFileActivity {

    /**
     * Processes a payments reporting file based on the provided IngestionFlowFile ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link PaymentsReportingIngestionFlowFileActivityResult} containing the list of IUFs and status.
     */
    PaymentsReportingIngestionFlowFileActivityResult processFile(Long ingestionFlowFileId);
}
