package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.AcaService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SynchronizeInstallmentAcaActivityImpl implements SynchronizeInstallmentAcaActivity {

    private final AcaService acaService;

    public SynchronizeInstallmentAcaActivityImpl(AcaService acaService) {
        this.acaService = acaService;
    }

    @Override
    public void synchronizeInstallmentAca(DebtPositionDTO debtPosition, String iud) {
        acaService.syncInstallmentAca(iud, debtPosition);
    }
}
