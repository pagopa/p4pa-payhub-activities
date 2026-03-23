package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;

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
	void UpdateSendNotificationStatus(String notificationRequestId, NotificationStatus newStatus);
}
