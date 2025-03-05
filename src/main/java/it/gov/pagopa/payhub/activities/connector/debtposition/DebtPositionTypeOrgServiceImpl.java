package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class DebtPositionTypeOrgServiceImpl implements DebtPositionTypeOrgService {

    private final AuthnService authnService;
    private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;

    public DebtPositionTypeOrgServiceImpl(AuthnService authnService, DebtPositionTypeOrgClient debtPositionTypeOrgClient) {
        this.authnService = authnService;
        this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
    }

    @Override
    public IONotificationDTO getIONotificationDetails(Long debtPositionTypeOrgId, String context) {
        String accessToken = authnService.getAccessToken();
        return debtPositionTypeOrgClient.getIONotificationDetails(accessToken, debtPositionTypeOrgId, context);
    }
}
