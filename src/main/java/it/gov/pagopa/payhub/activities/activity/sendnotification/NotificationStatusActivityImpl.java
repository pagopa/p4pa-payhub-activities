package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NewNotificationRequestStatusResponseV24DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class NotificationStatusActivityImpl implements NotificationStatusActivity {
    private final SendService sendService;

    public NotificationStatusActivityImpl(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public NewNotificationRequestStatusResponseV24DTO notificationStatus(String sendNotificationId) {
        log.info("Starting notificationStatus for sendNotificationId {}", sendNotificationId);
        return sendService.notificationStatus(sendNotificationId);
    }
}
