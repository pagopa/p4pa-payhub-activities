package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendNotificationClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationResponse;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SendNotificationServiceImpl implements SendNotificationService {
    private final SendNotificationClient sendNotificationClient;
    private final AuthnService authnService;

    public SendNotificationServiceImpl(SendNotificationClient sendNotificationClient, AuthnService authnService) {
        this.sendNotificationClient = sendNotificationClient;
        this.authnService = authnService;
    }

    @Override
    public SendNotificationDTO getSendNotification(String sendNotificationId) {
        return sendNotificationClient.findSendNotification(sendNotificationId, authnService.getAccessToken());
    }

    @Override
    public CreateNotificationResponse createSendNotification(CreateNotificationRequest createNotificationRequest) {
        return sendNotificationClient.createSendNotification(createNotificationRequest, authnService.getAccessToken());
    }
}
