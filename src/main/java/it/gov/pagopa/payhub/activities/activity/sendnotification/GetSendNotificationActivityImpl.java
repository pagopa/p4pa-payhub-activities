package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class GetSendNotificationActivityImpl implements GetSendNotificationActivity {

    private final SendNotificationService sendNotificationService;

    public GetSendNotificationActivityImpl(SendNotificationService sendNotificationService) {
        this.sendNotificationService = sendNotificationService;
    }

    @Override
    public SendNotificationDTO getSendNotification(String sendNotificationId) {
        log.info("Retrieve SendNotification {}", sendNotificationId);
        return sendNotificationService.getSendNotification(sendNotificationId);
    }
}
