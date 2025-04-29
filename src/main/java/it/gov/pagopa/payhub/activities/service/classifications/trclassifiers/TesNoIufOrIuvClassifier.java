package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class TesNoIufOrIuvClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, InstallmentNoPII installmentDTO, PaymentNotificationNoPII paymentNotificationDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf) {
		if (treasuryIuf == null || paymentNotificationDTO != null) {
			return null;
		}
		if (transferDTO == null || paymentsReportingDTO == null) {
			return ClassificationsEnum.TES_NO_IUF_OR_IUV;
		}
		return null;
	}
}
