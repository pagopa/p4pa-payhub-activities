package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.IngestionFlowFileProcessorActivity;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
/**
 * Interface for the PaymentsReportingIngestionFlowFileActivity.
 * Defines methods for processing payments reporting files based on an IngestionFlowFile ID.
 */
@ActivityInterface
public interface PaymentsReportingIngestionFlowFileActivity extends IngestionFlowFileProcessorActivity<PaymentsReportingIngestionFlowFileActivityResult> {

    /**
     * Processes a payments reporting file based on the provided IngestionFlowFile ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link PaymentsReportingIngestionFlowFileActivityResult} containing the list of IUFs and status.
     */
    @ActivityMethod
    @Override
    PaymentsReportingIngestionFlowFileActivityResult processFile(Long ingestionFlowFileId);
}
