package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface to start request to authorize preload file to SEND.
 */
@ActivityInterface
public interface PreloadSendFileActivity {

    /**
     * Start request to authorize preload file to SEND.
     *
     * @param sendNotificationId the ID of send notification
     */
    @ActivityMethod
    void preloadSendFile(String sendNotificationId);

}
