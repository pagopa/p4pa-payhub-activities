package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;

import java.util.List;

/**
 * Interface for the HandlePaymentsReportingDeletionActivity.
 * Defines methods for processing deletion of payments reporting with same organizationId, iuf and different ingestionFlowFileId.
 */
@ActivityInterface
public interface HandlePaymentsReportingDeletionActivity {

    /**
     * Processes deletion of payments reporting with same organizationId, iuf and different ingestionFlowFileId.
     *
     * @param organizationId the id of organization
     * @param iuf the iuf of payment reporting
     * @param ingestionFlowFileId the unique identifier related to the file processed.
     * @return the list of {@link PaymentsReportingTransferDTO} containing the payments reporting deleted.
     */
    @ActivityMethod
    List<PaymentsReportingTransferDTO> handlePaymentsReportingDeletion(Long organizationId, String iuf, Long ingestionFlowFileId);
}
