package it.gov.pagopa.payhub.activities.activity.paymentsreporting;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowActivityResult;

/**
 * Interface for the PaymentsReportingIngestionFlowActivity.
 * Defines methods for processing payments reporting files based on an IngestionFlow ID.
 */
public interface PaymentsReportingIngestionFlowActivity {

    /**
     * Processes a payments reporting file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowId the unique identifier related to the file to process.
     * @return {@link PaymentsReportingIngestionFlowActivityResult} containing the list of IUFs and status.
     */
    PaymentsReportingIngestionFlowActivityResult processFile(Long ingestionFlowId);
}
