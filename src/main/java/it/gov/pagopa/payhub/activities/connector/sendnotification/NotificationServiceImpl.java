package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.NotificationClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationClient notificationClient;
    private final AuthnService authnService;

    public NotificationServiceImpl(NotificationClient notificationClient, AuthnService authnService) {
        this.notificationClient = notificationClient;
        this.authnService = authnService;
    }

    @Override
    public SendNotificationDTO retrieveNotificationDate(String sendNotificationId, Long organizationId) {
        return notificationClient.retrieveNotificationDate(authnService.getAccessToken(), sendNotificationId, organizationId);
    }
}
