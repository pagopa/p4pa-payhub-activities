package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
}
