package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionSearchClient;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentClient;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.SendClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.sendnotification.dto.generated.NewNotificationRequestStatusResponseV24DTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
public class SendServiceImpl implements SendService {
    private final SendClient sendClient;
    private final InstallmentClient installmentClient;
    private final DebtPositionSearchClient debtPositionSearchClient;
    private final AuthnService authnService;

    public SendServiceImpl(SendClient sendClient, AuthnService authnService, InstallmentClient installmentClient, DebtPositionSearchClient debtPositionSearchClient) {
        this.sendClient = sendClient;
        this.authnService = authnService;
        this.installmentClient = installmentClient;
        this.debtPositionSearchClient = debtPositionSearchClient;
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

    @Override
    public SendNotificationDTO retrieveNotificationDate(String accessToken, String sendNotificationId, Long organizationId) {
        SendNotificationDTO sendNotificationDTO = sendClient.retrieveNotificationDate(accessToken, sendNotificationId, organizationId);
        if (sendNotificationDTO != null && sendNotificationDTO.getNotificationDate() != null) {
            OffsetDateTime notificationDate = sendNotificationDTO.getNotificationDate();
            List<InstallmentDTO> installmentList = new ArrayList<>();

            sendNotificationDTO.getPayments()
                    .forEach(sendNotificationPayments ->
                            sendNotificationPayments.getNavList()
                                    .forEach(nav -> {
                                        List<InstallmentDTO> installments = installmentClient.getInstallmentsByOrganizationIdAndNav(accessToken, organizationId, nav, null);

                                        installments.forEach(installmentDTO -> installmentDTO.setNotificationDate(notificationDate));

                                        installmentList.addAll(installments);

                                    }));

            installmentList.stream()
                    .filter(installmentDTO -> !InstallmentStatus.CANCELLED.equals(installmentDTO.getStatus()))
                    .forEach(installmentDTO -> {
                        //DebtPositionDTO debtPositionDTO = debtPositionSearchClient.findByInstallmentId(installmentDTO.getInstallmentId(), accessToken);
                    });
            return sendNotificationDTO;
        }
        return null;
    }

}
