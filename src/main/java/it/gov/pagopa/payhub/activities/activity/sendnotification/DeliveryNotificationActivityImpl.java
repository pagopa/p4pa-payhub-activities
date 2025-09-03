package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendNotificationConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
@Lazy
public class DeliveryNotificationActivityImpl implements DeliveryNotificationActivity {
    private final SendService sendService;

    public DeliveryNotificationActivityImpl(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void deliverySendNotification(String sendNotificationId) {
        log.info("Starting deliveryNotification for sendNotificationId {}", sendNotificationId);
        try {
            sendService.deliveryNotification(sendNotificationId);
        } catch (HttpClientErrorException.Conflict e) {
            log.error("Conflict error while deliverySendNotification for sendNotificationId {}", sendNotificationId, e);
            throw new SendNotificationConflictException("Conflict error while deliverySendNotification for sendNotificationId " + sendNotificationId);
        }
    }
}
