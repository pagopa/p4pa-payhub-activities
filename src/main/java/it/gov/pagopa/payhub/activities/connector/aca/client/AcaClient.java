package it.gov.pagopa.payhub.activities.connector.aca.client;

import it.gov.pagopa.payhub.activities.connector.aca.config.AcaApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class AcaClient {

    private final AcaApisHolder acaApisHolder;

    public AcaClient(AcaApisHolder acaApisHolder) {
        this.acaApisHolder = acaApisHolder;
    }

    public List<String> createAcaDebtPosition(DebtPositionDTO debtPositionDTO, String accessToken) {
        return acaApisHolder.getAcaApi(accessToken).createAca(debtPositionDTO);
    }

    public List<String> deleteAcaDebtPosition(DebtPositionDTO debtPositionDTO, String accessToken) {
        return acaApisHolder.getAcaApi(accessToken).deleteAca(debtPositionDTO);
    }
}
