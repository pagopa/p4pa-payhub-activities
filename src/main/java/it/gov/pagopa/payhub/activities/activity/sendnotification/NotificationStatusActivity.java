package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

/**
 * Interface to retrieve notification status from send notification.
 */
@ActivityInterface
public interface NotificationStatusActivity {

    /**
     * Retrieves the notification status from send notification.
     *
     * @param sendNotificationId the ID of send notification
     */
    @ActivityMethod
    SendNotificationDTO getSendNotificationStatus(String sendNotificationId);

}
