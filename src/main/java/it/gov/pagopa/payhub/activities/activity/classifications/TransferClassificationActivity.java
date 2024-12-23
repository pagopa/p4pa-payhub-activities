package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;

import java.util.List;

/**
 * Interface for defining an activity to process Transfer classifications.
 */
@ActivityInterface
public interface TransferClassificationActivity {

	/**
	 * Processes Transfer classification based on the provided parameters.
	 *
	 * @param orgId the unique identifier of the organization
	 * @param iuv   the unique identifier of the payment (IUV)
	 * @param iur   the identifier of the receipt associated with the payment
	 * @param transferIndex  the index of the transfer to be classified
	 */
	@ActivityMethod
	void classify(Long orgId, String iuv, String iur, int transferIndex);

	/**
	 * retrieve Transfer data by semantic key
	 *
	 * @param orgId the unique identifier of the organization
	 * @param iuv   the unique identifier of the payment (IUV)
	 * @param iur   the identifier of the receipt associated with the payment
	 * @param transferIndex  the index of the transfer to be classified
	 * @return List of PaymentsReportingDTO object containing data found (may be 1 object)
	 */
	@ActivityMethod
	List<PaymentsReportingDTO> retrievePaymentReportingBySemanticKey(Long orgId, String iuv, String iur, int transferIndex);
}