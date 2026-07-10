package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.TimelineElementCategoryV27DTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public void downloadAndArchiveSendLegalFact(String notificationRequestId, LegalFactCategoryDTO legalFactCategoryDTO, String legalFactId) {
        sendClient.downloadAndArchiveSendLegalFact(notificationRequestId, legalFactCategoryDTO, legalFactId, authnService.getAccessToken());
    }


    public void notifySendNotificationTimelineCategory(Map<String, List<TimelineElementCategoryV27DTO>> requestBody) {
        sendClient.notifySendNotificationTimelineCategory(requestBody, authnService.getAccessToken());
    }

}
