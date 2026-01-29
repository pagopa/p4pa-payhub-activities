package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

/**
 * Interface for updating send notification status.
 */
@ActivityInterface
public interface UpdateSendNotificationStatusActivity {
	/**
	 * Update notification status from send notification.
	 *
	 * @param notificationRequestId the ID of send notification request
	 */
	@ActivityMethod
	SendNotificationDTO updateSendNotificationStatus(String notificationRequestId);
}
