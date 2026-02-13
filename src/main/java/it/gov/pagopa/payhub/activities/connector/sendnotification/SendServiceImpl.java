package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactDownloadMetadataDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
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
        sendClient.preloadSendFile(sendNotificationId, authnService.getAccessToken());
    }

    @Override
    public void uploadSendFile(String sendNotificationId) {
        sendClient.uploadSendFile(sendNotificationId, authnService.getAccessToken());
    }

    @Override
    public void deliveryNotification(String sendNotificationId) {
        sendClient.deliveryNotification(sendNotificationId, authnService.getAccessToken());
    }

    @Override
    public SendNotificationDTO notificationStatus(String sendNotificationId) {
        return sendClient.notificationStatus(sendNotificationId, authnService.getAccessToken());
    }

    @Override
    public SendNotificationDTO retrieveNotificationDate(String sendNotificationId) {
        return sendClient.retrieveNotificationDate(sendNotificationId, authnService.getAccessToken());
    }

    @Override
    public SendNotificationDTO retrieveNotificationByNotificationRequestId(String notificationRequestId) {
        return sendClient.retrieveNotificationByNotificationRequestId(notificationRequestId, authnService.getAccessToken());
    }

    @Override
    public LegalFactDownloadMetadataDTO retrieveLegalFactDownloadMetadata(String sendNotificationId, String legalFactId) {
        return sendClient.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId, authnService.getAccessToken());
    }

}
