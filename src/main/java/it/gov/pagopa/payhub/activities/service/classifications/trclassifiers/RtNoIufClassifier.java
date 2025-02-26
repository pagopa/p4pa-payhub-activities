package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RtNoIufClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentsReporting paymentsReportingDTO, Treasury treasuryDTO) {
		if(transferDTO != null && paymentsReportingDTO == null && treasuryDTO == null) {
			return ClassificationsEnum.RT_NO_IUF;
		}
		if (transferDTO != null && paymentsReportingDTO != null && treasuryDTO == null && !getAmountCentsFromTransfer(transferDTO).equals(getAmountPaidCentsFromPaymentsReporting(paymentsReportingDTO))) {
			return ClassificationsEnum.RT_NO_IUF;
		}
		if (transferDTO != null && treasuryDTO != null && paymentsReportingDTO == null && !getAmountCentsFromTransfer(transferDTO).equals(getBillAmountCentsFromTreasury(treasuryDTO))) {
			return ClassificationsEnum.RT_NO_IUF;
		}
		return null;
	}
}
