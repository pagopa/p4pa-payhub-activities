package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

/**
 * Interface to retrieve notification date.
 */
@ActivityInterface
public interface SendNotificationDateRetrieveActivity {

    /**
     * Retrieve notification date process.
     *
     * @param notificationRequestId the ID of send notification request
     */
    @ActivityMethod
    SendNotificationDTO sendNotificationDateRetrieve(String notificationRequestId);

}
