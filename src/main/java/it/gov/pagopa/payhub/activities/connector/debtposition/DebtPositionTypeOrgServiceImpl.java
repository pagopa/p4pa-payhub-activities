package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
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
    public IONotificationDTO getIONotificationDetails(Long debtPositionTypeOrgId, NotificationRequestDTO.OperationTypeEnum context) {
        log.info("Fetching IO Notification details for debtPositionTypeOrgId: {} and operationType: {}", debtPositionTypeOrgId, context);
        try {
            String accessToken = authnService.getAccessToken();
            return debtPositionTypeOrgClient.getIONotificationDetails(accessToken, debtPositionTypeOrgId, context);
        } catch (Exception e) {
            log.error("Failed to retrieve IO Notification details for debtPositionTypeOrgId: {} and operationType: {}", debtPositionTypeOrgId, context, e);
            return null;
        }
    }
}
