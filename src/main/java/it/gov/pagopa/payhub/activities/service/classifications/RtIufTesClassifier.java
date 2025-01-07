package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Lazy
@Component
public class RtIufTesClassifier implements LabelClassifier {

	@Override
	public ClassificationsEnum define(TransferDTO transferDTO, PaymentsReportingDTO paymentsReportingDTO, TreasuryDTO treasuryDTO) {
		if(transferDTO != null && paymentsReportingDTO != null && treasuryDTO != null &&
			Objects.equals(transferDTO.getAmount(), paymentsReportingDTO.getAmountPaidCents()) &&
			Objects.equals(paymentsReportingDTO.getTotalAmountCents(), treasuryDTO.getBillIpNumber().movePointRight(2).longValueExact())) {
			return ClassificationsEnum.RT_IUF_TES;
		}
		return null;
	}
}
