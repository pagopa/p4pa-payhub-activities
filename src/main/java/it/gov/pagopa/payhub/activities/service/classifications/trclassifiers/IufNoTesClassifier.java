package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class IufNoTesClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentsReporting paymentsReportingDTO, Treasury treasuryDTO) {
		if(paymentsReportingDTO != null && treasuryDTO == null) {
			return ClassificationsEnum.IUF_NO_TES;
		}
		return null;
	}
}
