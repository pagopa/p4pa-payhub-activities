package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

/**
 * Interface to retrieve notification date.
 */
@ActivityInterface
public interface RetrieveNotificationDateActivity {

    /**
     * Retrieve notification date process.
     *
     * @param sendNotificationId the ID of send notification
     */
    @ActivityMethod
    SendNotificationDTO retrieveNotificationDate(String sendNotificationId);

}
