package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Lazy
@Component
public class IuvNoRtClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentNotificationNoPII paymentNotificationNoPII, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf, Optional<InstallmentNoPII> installmentDTO) {
		if(paymentsReportingDTO != null && transferDTO == null) {
			return ClassificationsEnum.IUV_NO_RT;
		}
		return null;
	}
}
