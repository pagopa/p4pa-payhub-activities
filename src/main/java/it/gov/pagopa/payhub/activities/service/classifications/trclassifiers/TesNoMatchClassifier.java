package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

public class TesNoMatchClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentNotificationNoPII paymentNotificationDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf) {
		if (transferDTO == null && paymentNotificationDTO == null && paymentsReportingDTO == null && treasuryIuf != null &&
			treasuryIuf.getIuf() == null && treasuryIuf.getIuv() == null) {
			return ClassificationsEnum.TES_NO_MATCH;
		}
		return null;
	}
}
