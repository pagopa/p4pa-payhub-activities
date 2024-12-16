package it.gov.pagopa.payhub.activities.activity.ionotification;

import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;

public interface SendIONotificationActivity {

    void sendMessage(NotificationQueueDTO notificationQueueDTO);
}
