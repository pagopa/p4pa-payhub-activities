package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

import java.util.Optional;

/**
 * Interface for defining classification logic for transfers based on provided data.
 *
 * Implementations of this interface contain specific rules to evaluate
 * and determine a classification label ({@link ClassificationsEnum}). Each implementation
 * should handle a particular condition or set of conditions to decide the appropriate label.
 */
public interface TransferClassifier {

	/**
	 * Defines a classification label ({@link ClassificationsEnum}) based on the given data.
	 *
	 * @param transferDTO the transfer data.
	 * @param paymentsReportingDTO the payment reporting data.
	 * @param treasuryDTO the treasury data.
	 * @return the classification label if conditions are met, or {@code null} if no label is applicable.
	 */
	ClassificationsEnum classify(Transfer transferDTO, PaymentsReporting paymentsReportingDTO, Treasury treasuryDTO);

	/**
	 * Extracts the amount in cents from the transfer data.
	 *
	 * @param transferDTO the transfer data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getAmountCents(Transfer transferDTO) {
		return Optional.ofNullable(transferDTO).map(Transfer::getAmountCents).orElse(0L);
	}

	/**
	 * Extracts the amount in cents from the payment reporting data.
	 *
	 * @param paymentsReportingDTO the payment reporting data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getTransferAmountCents(PaymentsReporting paymentsReportingDTO) {
		return Optional.ofNullable(paymentsReportingDTO).map(PaymentsReporting::getAmountPaidCents).orElse(0L);
	}

	/**
	 * Extracts the total amount in cents from the payment reporting data.
	 *
	 * @param paymentsReportingDTO the payment reporting data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getIufAmountCents(PaymentsReporting paymentsReportingDTO) {
		return Optional.ofNullable(paymentsReportingDTO).map(PaymentsReporting::getTotalAmountCents).orElse(0L);
	}

	/**
	 * Extracts the amount in cents from the treasury data.
	 *
	 * @param treasury the treasury data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getIufAmountCents(Treasury treasury) {
		return Optional.ofNullable(treasury).map(Treasury::getBillAmountCents).orElse(0L);
	}
}


