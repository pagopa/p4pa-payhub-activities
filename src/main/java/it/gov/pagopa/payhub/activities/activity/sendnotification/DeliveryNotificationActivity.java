package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface to delivery notification process.
 */
@ActivityInterface
public interface DeliveryNotificationActivity {

    /**
     * Delivery notification process.
     *
     * @param sendNotificationId the ID of send notification
     */
    @ActivityMethod
    void deliveryNotification(String sendNotificationId);

}
