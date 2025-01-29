package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RtIufTesClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentsReporting paymentsReportingDTO, Treasury treasuryDTO) {
		if(transferDTO != null && paymentsReportingDTO != null && treasuryDTO != null &&
			getAmountCents(transferDTO).equals(getAmountCents(paymentsReportingDTO)) &&
			getAmountCents(transferDTO).equals(getAmountCents(treasuryDTO))) {
			return ClassificationsEnum.RT_IUF_TES;
		}
		return null;
	}
}
