package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuv;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RtTesClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf, TreasuryIuv treasuryIuv) {
		if(transferDTO != null && treasuryIuv != null &&
			getAmountCents(transferDTO).equals(getTransferAmountCents(treasuryIuv))) {
			return ClassificationsEnum.RT_TES;
		}
		return null;
	}
}
