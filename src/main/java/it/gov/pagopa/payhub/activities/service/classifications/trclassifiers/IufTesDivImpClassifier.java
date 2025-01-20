package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class IufTesDivImpClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(TransferDTO transferDTO, PaymentsReporting paymentsReportingDTO, Treasury treasuryDTO) {
		if (paymentsReportingDTO != null && treasuryDTO != null	&& !getAmountCents(paymentsReportingDTO).equals(treasuryDTO.getBillAmountCents())) {
			return ClassificationsEnum.IUF_TES_DIV_IMP;
		}
		return null;
	}
}
