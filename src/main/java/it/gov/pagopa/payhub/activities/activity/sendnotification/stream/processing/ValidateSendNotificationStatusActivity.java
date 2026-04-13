package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

/**
 * Interface for validity send notification status.
 */
@ActivityInterface
public interface ValidateSendNotificationStatusActivity {
	/**
	 * Validate notification status from send notification.
	 *
	 * @param notificationRequestId the ID of send notification request
	 */
	@ActivityMethod
	SendNotificationDTO validateSendNotificationStatus(String notificationRequestId);
}
