package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;

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
	Long getAmountCents(TransferDTO transferDTO);

	/**
	 * Extracts the amount in cents from the payment reporting data.
	 *
	 * @param paymentsReportingDTO the payment reporting data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	Long getAmountCents(PaymentsReportingDTO paymentsReportingDTO);

	/**
	 * Extracts the amount in cents from the treasury data.
	 *
	 * @param treasuryDTO the treasury data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	Long getAmountCents(TreasuryDTO treasuryDTO);
}


