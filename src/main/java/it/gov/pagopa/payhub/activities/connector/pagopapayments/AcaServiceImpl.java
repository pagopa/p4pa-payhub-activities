package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.AcaClient;
import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
    public void syncInstallmentAca(String iud, DebtPositionDTO debtPositionDTO) {
        String accessToken = authnService.getAccessToken();
        acaClient.syncAca(iud, debtPositionDTO, accessToken);
    }

}
