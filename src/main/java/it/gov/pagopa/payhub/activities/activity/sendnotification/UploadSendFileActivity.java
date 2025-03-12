package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface to load file to SEND archive process.
 */
@ActivityInterface
public interface UploadSendFileActivity {

    /**
     * Load file to SEND archive process.
     *
     * @param sendNotificationId the ID of send notification
     */
    @ActivityMethod
    void uploadSendFile(String sendNotificationId);

}
