package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuv;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RtIufTesClassifier implements TransferClassifier {

    @Override
    public ClassificationsEnum classify(Transfer transferDTO, PaymentsReporting paymentsReportingDTO, TreasuryIuf treasuryIuf, TreasuryIuv treasuryIuv) {
        if (transferDTO != null && paymentsReportingDTO != null && (treasuryIuf != null || treasuryIuv != null) &&
            getAmountCents(transferDTO).equals(getTransferAmountCents(paymentsReportingDTO)) &&
            (treasuryIuf == null || getIufAmountCents(paymentsReportingDTO).equals(getIufAmountCents(treasuryIuf))) &&
            (treasuryIuv == null || getTransferAmountCents(paymentsReportingDTO).equals(getTransferAmountCents(treasuryIuv)))
        ) {
            return ClassificationsEnum.RT_IUF_TES;
        }
        return null;
    }
}
