package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.client.GpdClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class GpdServiceImpl implements GpdService {

    private final GpdClient gpdClient;
    private final AuthnService authnService;

    public GpdServiceImpl(GpdClient gpdClient, AuthnService authnService) {
        this.gpdClient = gpdClient;
        this.authnService = authnService;
    }

    @Override
    public String syncInstallmentGpd(String iud, DebtPositionDTO debtPositionDTO) {
        String accessToken = authnService.getAccessToken();
        gpdClient.syncGpd(iud, debtPositionDTO, accessToken);
        return debtPositionDTO.getPaymentOptions().stream()
                .flatMap(paymentOptionDTO -> paymentOptionDTO.getInstallments().stream())
                .filter(installmentDTO -> iud.equals(installmentDTO.getIud()))
                .findFirst()
                .map(InstallmentDTO::getIupdPagopa)
                .orElse(null);
    }
}
