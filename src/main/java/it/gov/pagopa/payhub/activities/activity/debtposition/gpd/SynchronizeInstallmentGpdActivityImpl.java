package it.gov.pagopa.payhub.activities.activity.debtposition.gpd;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.GpdService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class SynchronizeInstallmentGpdActivityImpl implements SynchronizeInstallmentGpdActivity {

    private final GpdService gpdService;

    public SynchronizeInstallmentGpdActivityImpl(GpdService gpdService) {
        this.gpdService = gpdService;
    }

    @Override
    public void synchronizeInstallmentGpd(DebtPositionDTO debtPosition, String iud) {
        log.info("Synchronizing IUD {} of DebtPosition {} with GPD", iud, debtPosition.getDebtPositionId());
        gpdService.syncInstallmentGpd(iud, debtPosition);
    }
}
