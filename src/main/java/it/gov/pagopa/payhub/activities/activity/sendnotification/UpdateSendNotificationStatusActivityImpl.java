package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class UpdateSendNotificationStatusActivityImpl implements UpdateSendNotificationStatusActivity {

    private final SendNotificationService sendNotificationService;

    public UpdateSendNotificationStatusActivityImpl(SendNotificationService sendNotificationService) {
        this.sendNotificationService = sendNotificationService;
    }

    @Override
    public void updateSendNotificationStatus(String notificationRequestId, NotificationStatus newStatus) {
        sendNotificationService.updateSendNotificationStatus(notificationRequestId, newStatus);
    }
}
