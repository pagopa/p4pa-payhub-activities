package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class DebtPositionTypeOrgServiceImpl implements DebtPositionTypeOrgService {

    private final AuthnService authnService;
    private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;

    public DebtPositionTypeOrgServiceImpl(AuthnService authnService, DebtPositionTypeOrgClient debtPositionTypeOrgClient) {
        this.authnService = authnService;
        this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
    }

    @Override
    public DebtPositionTypeOrg getById(Long debtPositionTypeOrgId) {
        return debtPositionTypeOrgClient.findById(debtPositionTypeOrgId, authnService.getAccessToken());
    }

    @Override
    public IONotificationDTO getDefaultIONotificationDetails(Long debtPositionTypeOrgId, PaymentEventType paymentEventType) {
        log.info("Fetching IO Notification details for debtPositionTypeOrgId: {} and paymentEventType: {}", debtPositionTypeOrgId, paymentEventType);
        try {
            // To be updated when debtPositionTypeOrg supports additional PaymentEventType values
            if (paymentEventType != PaymentEventType.DP_CREATED) {
                return null;
            }
            String accessToken = authnService.getAccessToken();
            return debtPositionTypeOrgClient.getIONotificationDetails(debtPositionTypeOrgId, paymentEventType, accessToken);
        } catch (Exception e) {
            log.error("Failed to retrieve IO Notification details for debtPositionTypeOrgId: {} and paymentEventType: {}", debtPositionTypeOrgId, paymentEventType, e);
            return null;
        }
    }
}
