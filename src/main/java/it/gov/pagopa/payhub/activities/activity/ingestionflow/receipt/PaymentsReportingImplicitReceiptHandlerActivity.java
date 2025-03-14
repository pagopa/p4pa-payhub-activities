package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;

/**
 * Interface for the PaymentsReportingImplicitReceiptHandlerActivity.
 * Defines methods for generate dummy Receipt due to outcome code (8 or 9) in Payments Reporting.
 */
@ActivityInterface
public interface PaymentsReportingImplicitReceiptHandlerActivity {
	/**
	 * Generate dummy receipt based on Payment Reporting data
	 *
	 * @param paymentsReportingTransferDTO Object contains Transfer Semantic Key and payment outcome code
	 */
	@ActivityMethod
	void handleImplicitReceipt(PaymentsReportingTransferDTO paymentsReportingTransferDTO);
}
