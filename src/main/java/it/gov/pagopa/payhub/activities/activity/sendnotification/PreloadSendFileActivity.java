package it.gov.pagopa.payhub.activities.activity.sendnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PreloadSendFileActivity {

    @ActivityMethod
    void preloadSendFile(String sendNotificationId);

}
