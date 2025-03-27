package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

/** Activity to fetch the {@link SendNotificationDTO} given a sendNotificationId */
@ActivityInterface
public interface GetSendNotificationActivity {
    @ActivityMethod
    SendNotificationDTO getSendNotification(String sendNotificationId);
}
