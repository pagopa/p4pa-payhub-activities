package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

import java.util.Optional;

/**
 * Interface for defining classification logic for transfers based on provided data.
 * <BR/>
 * Implementations of this interface contain specific rules to evaluate
 * and determine a classification label ({@link ClassificationsEnum}). Each implementation
 * should handle a particular condition or set of conditions to decide the appropriate label.
 */
public interface TransferClassifier {

	/**
	 * Defines a classification label ({@link ClassificationsEnum}) based on the given data.
	 *
	 * @param transferDTO            the transfer data.
	 * @param installmentDTO         the installment data.
	 * @param paymentNotificationDTO the payment notification data.
	 * @param paymentsReportingDTO   the payment reporting data.
	 * @param treasuryIuf            the treasury IUF data.
	 * @return the classification label if conditions are met, or {@code null} if no label is applicable.
	 */
	ClassificationsEnum classify(Transfer transferDTO, InstallmentNoPII installmentDTO, PaymentNotificationNoPII paymentNotificationDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf);

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
	 * @param treasuryIuf the treasury data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getIufAmountCents(TreasuryIuf treasuryIuf) {
		return Optional.ofNullable(treasuryIuf).map(Treasury::getBillAmountCents).orElse(0L);
	}

	/**
	 * Extracts the amount in cents from the payment notification data.
	 *
	 * @param paymentNotificationNoPII the payment notification data.
	 * @return the amount in cents, or {@code null} if the amount is not available.
	 */
	default Long getIudAmountCents(PaymentNotificationNoPII paymentNotificationNoPII) {
		return Optional.ofNullable(paymentNotificationNoPII).map(PaymentNotificationNoPII::getAmountPaidCents).orElse(0L);
	}
}


