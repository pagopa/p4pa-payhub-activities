package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendNotificationClient;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public SendNotificationDTO findSendNotificationByOrgIdAndNav(Long organizationId, String nav) {
        return sendNotificationClient.findSendNotificationByOrgIdAndNav(organizationId, nav, authnService.getAccessToken());
    }

    @Override
    public StartNotificationResponse startSendNotification(String sendNotificationId, LoadFileRequest loadFileRequest) {
        return sendNotificationClient.startSendNotification(sendNotificationId, loadFileRequest, authnService.getAccessToken());
    }

    @Override
    public SendStreamDTO findSendStream(String sendStreamId) {
        return sendNotificationClient.findSendStream(sendStreamId, authnService.getAccessToken());
    }

    @Override
    public List<ProgressResponseElementV25DTO> readSendStreamEvents(Long organizationId, String sendStreamId, String lastEventId) {
        return sendNotificationClient.readSendStreamEvents(organizationId, sendStreamId, lastEventId, authnService.getAccessToken());
    }
}
