package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Lazy
@Component
public class RtIufClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(TransferDTO transferDTO, PaymentsReportingDTO paymentsReportingDTO, TreasuryDTO treasuryDTO) {
		if (transferDTO != null && paymentsReportingDTO != null && getAmountCents(transferDTO).equals(getAmountCents(paymentsReportingDTO))) {
			return ClassificationsEnum.RT_IUF;
		}
		return null;
	}

	@Override
	public Long getAmountCents(TransferDTO transferDTO) {
		return Optional.ofNullable(transferDTO).map(TransferDTO::getAmount).orElse(0L);
	}

	@Override
	public Long getAmountCents(PaymentsReportingDTO paymentsReportingDTO) {
		return Optional.ofNullable(paymentsReportingDTO).map(PaymentsReportingDTO::getAmountPaidCents).orElse(0L);
	}

	@Override
	public Long getAmountCents(TreasuryDTO treasuryDTO) {
		return Optional.ofNullable(treasuryDTO)
			.map(item -> item.getBillIpNumber().movePointRight(2).longValueExact())
			.orElse(0L);
	}
}
