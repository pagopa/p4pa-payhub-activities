package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

/** Activity to fetch the {@link SendNotificationDTO} given a notificationRequestId */
@ActivityInterface
public interface GetSendNotificationByNotificationRequestIdActivity {
    @ActivityMethod
    SendNotificationDTO getSendNotificationByNotificationRequestId(String notificationRequestId);
}