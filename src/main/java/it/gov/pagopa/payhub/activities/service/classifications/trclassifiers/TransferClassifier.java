package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;

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
	ClassificationsEnum classify(TransferDTO transferDTO, PaymentsReportingDTO paymentsReportingDTO, TreasuryDTO treasuryDTO);

	/**
	 * Extracts the amount in cents from the transfer data.
	 *
	 * @param transferDTO the transfer data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getAmountCents(TransferDTO transferDTO) {
		return Optional.ofNullable(transferDTO).map(TransferDTO::getAmount).orElse(0L);
	}

	/**
	 * Extracts the amount in cents from the payment reporting data.
	 *
	 * @param paymentsReportingDTO the payment reporting data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getAmountCents(PaymentsReportingDTO paymentsReportingDTO) {
		return Optional.ofNullable(paymentsReportingDTO).map(PaymentsReportingDTO::getAmountPaidCents).orElse(0L);
	}

	/**
	 * Extracts the amount in cents from the treasury data.
	 *
	 * @param treasuryDTO the treasury data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getAmountCents(TreasuryDTO treasuryDTO) {
		return Optional.ofNullable(treasuryDTO)
			.map(item -> item.getBillIpNumber().movePointRight(2).longValueExact())
			.orElse(0L);
	}
}


