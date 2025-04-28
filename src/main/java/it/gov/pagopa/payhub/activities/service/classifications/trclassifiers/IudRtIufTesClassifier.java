package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
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
public class IudRtIufTesClassifier implements TransferClassifier {
	private final InstallmentService installmentService;

	public IudRtIufTesClassifier(InstallmentService installmentService) {
		this.installmentService = installmentService;
	}

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentNotificationNoPII paymentNotificationDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf) {
		if (transferDTO != null && paymentNotificationDTO != null && paymentsReportingDTO != null && treasuryIuf != null &&
			installmentService.getInstallmentById(transferDTO.getInstallmentId())
				.map(InstallmentNoPII::getAmountCents)
				.filter(getIudAmountCents(paymentNotificationDTO)::equals)
				.isPresent()) {
			return ClassificationsEnum.IUD_RT_IUF_TES;
		}
		return null;
	}
}
