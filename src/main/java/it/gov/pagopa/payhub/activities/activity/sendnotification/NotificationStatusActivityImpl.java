package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class NotificationStatusActivityImpl implements NotificationStatusActivity {
    private final SendService sendService;
    private final SendNotificationService sendNotificationService;
    private final InstallmentService installmentService;

    public NotificationStatusActivityImpl(SendService sendService, SendNotificationService sendNotificationService,
                                          InstallmentService installmentService) {
        this.sendService = sendService;
        this.sendNotificationService = sendNotificationService;
        this.installmentService = installmentService;
    }

    @Override
    public SendNotificationDTO getSendNotificationStatus(String sendNotificationId) {
        log.info("Starting notificationStatus for sendNotificationId {}", sendNotificationId);
        try {
            SendNotificationDTO sendNotificationDTO = sendService.notificationStatus(sendNotificationId);
            if (sendNotificationDTO != null && sendNotificationDTO.getIun() != null) {
                sendNotificationDTO.getPayments().forEach(p ->
                        installmentService.updateIunByDebtPositionId(p.getDebtPositionId(), sendNotificationDTO.getIun()));
            }
            return sendNotificationDTO;
        } catch (Exception ex) {
            log.error("Error calling getSendNotificationStatus for sendNotificationId: {}: {}", sendNotificationId, ex.getMessage());
            sendNotificationService.updateNotificationStatus(sendNotificationId, NotificationStatus.ERROR.getValue());
            throw ex;
        }
    }
}
