package it.gov.pagopa.payhub.activities.connector.aca;

import it.gov.pagopa.payhub.activities.connector.aca.client.AcaClient;
import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class AcaServiceImpl implements AcaService {

    private final AcaClient acaClient;
    private final AuthnService authnService;

    public AcaServiceImpl(AcaClient acaClient, AuthnService authnService) {
        this.acaClient = acaClient;
        this.authnService = authnService;
    }

    @Override
    public List<String> createAcaDebtPosition(DebtPositionDTO debtPositionDTO) {
        String accessToken = authnService.getAccessToken();
        return acaClient.createAcaDebtPosition(debtPositionDTO, accessToken);
    }

    @Override
    public List<String> deleteAcaDebtPosition(DebtPositionDTO debtPositionDTO) {
        String accessToken = authnService.getAccessToken();
        return acaClient.deleteAcaDebtPosition(debtPositionDTO, accessToken);
    }
}
