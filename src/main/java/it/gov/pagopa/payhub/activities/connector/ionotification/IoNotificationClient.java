package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.pu.p4paionotification.dto.generated.NotificationQueueDTO;

public interface IoNotificationClient {
    void sendMessage(NotificationQueueDTO notificationQueueDTO);
}
