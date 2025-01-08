package it.gov.pagopa.payhub.activities.connector.ionotification.client;

import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;

public interface IoNotificationClient {
    void sendMessage(NotificationQueueDTO notificationQueueDTO);
}
