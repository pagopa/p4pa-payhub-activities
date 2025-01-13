package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
/**
 * Interface for the PaymentsReportingIngestionFlowFileActivity.
 * Defines methods for processing payments reporting files based on an IngestionFlowFile ID.
 */
@ActivityInterface
public interface PaymentsReportingIngestionFlowFileActivity {

    /**
     * Processes a payments reporting file based on the provided IngestionFlowFile ID.
     *
     * @param ingestionFlowFileId the unique identifier related to the file to process.
     * @return {@link PaymentsReportingIngestionFlowFileActivityResult} containing the list of IUFs and status.
     */
    @ActivityMethod
    PaymentsReportingIngestionFlowFileActivityResult processFile(Long ingestionFlowFileId);
}
