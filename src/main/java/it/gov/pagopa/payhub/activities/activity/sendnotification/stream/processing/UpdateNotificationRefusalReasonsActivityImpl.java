package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationRefusedErrorV27DTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class UpdateNotificationRefusalReasonsActivityImpl implements UpdateNotificationRefusalReasonsActivity {

    private final SendNotificationService sendNotificationService;

    public UpdateNotificationRefusalReasonsActivityImpl(SendNotificationService sendNotificationService) {
        this.sendNotificationService = sendNotificationService;
    }

    @Override
    public void updateNotificationRefusalReasons(String notificationRequestId, List<NotificationRefusedErrorV27DTO> refusalReasons) {
        sendNotificationService.updateNotificationRefusalReasons(notificationRequestId, refusalReasons);
    }
}
