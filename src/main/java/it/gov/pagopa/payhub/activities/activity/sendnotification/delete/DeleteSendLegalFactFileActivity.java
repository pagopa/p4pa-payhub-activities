package it.gov.pagopa.payhub.activities.activity.sendnotification.delete;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.time.OffsetDateTime;

/**
 * Interface to delete send notification legal fact process.
 */
@ActivityInterface
public interface DeleteSendLegalFactFileActivity {

    /**
     * delete send notification legal fact process.
     *
     * @param sendNotificationId the ID of send notification
     * @return the OffsetDateTime of the next schedule
     */
    @ActivityMethod
    OffsetDateTime deleteSendLegalFactFile(String sendNotificationId);

}
