package it.gov.pagopa.payhub.activities.connector.aca.client;

import it.gov.pagopa.payhub.activities.connector.aca.config.AcaApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AcaClient {

    private final AcaApisHolder acaApisHolder;

    public AcaClient(AcaApisHolder acaApisHolder) {
        this.acaApisHolder = acaApisHolder;
    }

    public void createAcaDebtPosition(DebtPositionDTO debtPositionDTO, String accessToken) {
        acaApisHolder.getAcaApi(accessToken)
                .createAca(debtPositionDTO);
    }
}
