package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RtTesClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(TransferDTO transferDTO, PaymentsReportingDTO paymentsReportingDTO, Treasury treasuryDTO) {
		if(transferDTO != null && treasuryDTO != null && paymentsReportingDTO == null &&
			getAmountCents(transferDTO).equals(treasuryDTO.getBillAmountCents())) {
			return ClassificationsEnum.RT_TES;
		}
		return null;
	}
}
