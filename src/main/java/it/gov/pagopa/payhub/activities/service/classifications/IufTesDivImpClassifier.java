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
public class IufTesDivImpClassifier implements LabelClassifier {

	@Override
	public ClassificationsEnum define(TransferDTO transferDTO, PaymentsReportingDTO paymentsReportingDTO, TreasuryDTO treasuryDTO) {
		if (paymentsReportingDTO != null && treasuryDTO != null	&&
			Objects.equals(paymentsReportingDTO.getTotalAmountCents(), treasuryDTO.getBillIpNumber().movePointRight(2).longValueExact())) {
			return ClassificationsEnum.IUF_TES_DIV_IMP;
		}
		return null;
	}
}
