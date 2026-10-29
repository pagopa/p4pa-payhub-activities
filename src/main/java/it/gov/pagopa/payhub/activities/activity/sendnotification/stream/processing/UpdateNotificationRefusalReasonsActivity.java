package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationRefusedErrorV27DTO;

import java.util.List;

/**
 * Interface for saving/updating send notification refusal reasons.
 */
@ActivityInterface
public interface UpdateNotificationRefusalReasonsActivity {
    /**
     * Save/Update notification refusal reasons from send notification.
     *
     * @param notificationRequestId the ID of send notification request
     */
    @ActivityMethod
    void updateNotificationRefusalReasons(String notificationRequestId, List<NotificationRefusedErrorV27DTO> refusalReasons);
}
