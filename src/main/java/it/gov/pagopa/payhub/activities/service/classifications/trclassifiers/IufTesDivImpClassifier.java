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
public class IufTesDivImpClassifier implements TransferClassifier {

	@Override
	public ClassificationsEnum classify(Transfer transferDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf, TreasuryIuv treasuryIuv) {
		if (paymentsReportingDTO != null && treasuryIuf != null	&& !getIufAmountCents(paymentsReportingDTO).equals(getIufAmountCents(treasuryIuf))) {
			return ClassificationsEnum.IUF_TES_DIV_IMP;
		}
		return null;
	}
}
