package it.gov.pagopa.payhub.activities.activity.debtposition.gpdpreload;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.GpdPreLoadService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class SynchronizeInstallmentGpdPreLoadActivityImpl implements SynchronizeInstallmentGpdPreLoadActivity {

    private final GpdPreLoadService gpdPreLoadService;

    public SynchronizeInstallmentGpdPreLoadActivityImpl(GpdPreLoadService gpdPreLoadService) {
        this.gpdPreLoadService = gpdPreLoadService;
    }

    @Override
    public void synchronizeInstallmentGpdPreLoad(DebtPositionDTO debtPosition, String iud) {
        log.info("Synchronizing IUD {} of DebtPosition {} with GPD PreLoad", iud, debtPosition.getDebtPositionId());
        gpdPreLoadService.syncInstallmentGpdPreLoad(iud, debtPosition);
    }
}
