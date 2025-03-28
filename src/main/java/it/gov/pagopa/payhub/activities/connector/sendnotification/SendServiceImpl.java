package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.debtposition.dto.generated.UpdateInstallmentNotificationDateRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Lazy
@Service
public class SendServiceImpl implements SendService {
    private final SendClient sendClient;
    private final AuthnService authnService;
    private final DebtPositionClient debtPositionClient;

    public SendServiceImpl(SendClient sendClient, AuthnService authnService, DebtPositionClient debtPositionClient) {
        this.sendClient = sendClient;
        this.authnService = authnService;
        this.debtPositionClient = debtPositionClient;
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
    public SendNotificationDTO retrieveNotificationDate(String accessToken, String sendNotificationId, Long organizationId) {
        SendNotificationDTO sendNotificationDTO = sendClient.retrieveNotificationDate(accessToken, sendNotificationId);
        if (sendNotificationDTO != null && sendNotificationDTO.getNotificationDate() != null) {
            OffsetDateTime notificationDate = sendNotificationDTO.getNotificationDate();

            sendNotificationDTO.getPayments()
                    .forEach(sendNotificationPayments ->
                            sendNotificationPayments.getNavList()
                                    .forEach(nav -> {
                                        UpdateInstallmentNotificationDateRequest request = UpdateInstallmentNotificationDateRequest.builder()
                                                .debtPositionId(sendNotificationPayments.getDebtPositionId())
                                                .nav(nav)
                                                .notificationDate(notificationDate)
                                                .build();

                                        debtPositionClient.updateInstallmentNotificationDate(accessToken, request);
                                    }));

            return sendNotificationDTO;
        }
        return null;
    }

}
