package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.NewNotificationRequestStatusResponseV24DTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SendServiceImpl implements SendService {
    private final SendClient sendClient;
    private final AuthnService authnService;

    public SendServiceImpl(SendClient sendClient, AuthnService authnService) {
        this.sendClient = sendClient;
        this.authnService = authnService;
    }

    @Override
    public void preloadSendFile(String sendNotificationId) {
        sendClient.preloadSendFile(authnService.getAccessToken(), sendNotificationId);
    }

    @Override
    public void uploadSendFile(String sendNotificationId) {
        sendClient.uploadSendFile(authnService.getAccessToken(), sendNotificationId);
    }

    @Override
    public void deliveryNotification(String sendNotificationId) {
        sendClient.deliveryNotification(authnService.getAccessToken(), sendNotificationId);
    }

    @Override
    public NewNotificationRequestStatusResponseV24DTO notificationStatus(String sendNotificationId) {
        return sendClient.notificationStatus(authnService.getAccessToken(), sendNotificationId);
    }
}
