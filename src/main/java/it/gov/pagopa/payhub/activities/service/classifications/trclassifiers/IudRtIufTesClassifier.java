package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

public class IudRtIufTesClassifier implements TransferClassifier{

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentNotificationNoPII paymentNotificationDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf) {
		if (transferDTO != null && paymentNotificationDTO != null && paymentsReportingDTO != null && treasuryIuf != null &&
			getAmountCents(transferDTO).equals(getTransferAmountCents(paymentsReportingDTO)) &&
			getTransferAmountCents(paymentsReportingDTO).equals(getIudAmountCents(paymentNotificationDTO)) &&
			getIudAmountCents(paymentNotificationDTO).equals(getIufAmountCents(treasuryIuf))
		) {
			return ClassificationsEnum.IUD_RT_IUF_TES;
		}
		return null;
	}
}
