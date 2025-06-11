package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
        sendService.deliveryNotification(sendNotificationId);
    }
}
