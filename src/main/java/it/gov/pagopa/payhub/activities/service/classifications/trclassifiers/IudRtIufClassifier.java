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
public class IudRtIufClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentNotificationNoPII paymentNotificationDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf, Optional<InstallmentNoPII> installmentDTO) {
		if (transferDTO != null && paymentNotificationDTO != null && paymentsReportingDTO != null &&
			getAmountCents(transferDTO).equals(getTransferAmountCents(paymentsReportingDTO)) &&
			installmentDTO.map(InstallmentNoPII::getAmountCents)
				.filter(getIudAmountCents(paymentNotificationDTO)::equals)
				.isPresent()) {
			return ClassificationsEnum.IUD_NO_RT;
		}
		return null;
	}
}
