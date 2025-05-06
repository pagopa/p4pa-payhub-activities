package it.gov.pagopa.payhub.activities.activity.debtposition.synchronize.aca;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.AcaService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class SynchronizeInstallmentAcaActivityImpl implements SynchronizeInstallmentAcaActivity {

    private final AcaService acaService;

    public SynchronizeInstallmentAcaActivityImpl(AcaService acaService) {
        this.acaService = acaService;
    }

    @Override
    public void synchronizeInstallmentAca(DebtPositionDTO debtPosition, String iud) {
        log.info("Synchronizing IUD {} of DebtPosition {} with ACA", iud, debtPosition.getDebtPositionId());
        acaService.syncInstallmentAca(iud, debtPosition);
    }
}
