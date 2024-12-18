package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;


/**
 * This interface provides a method for sending notification messages to the microservice IO Notification.
 */
public interface SendDebtPositionIONotificationService {

    /**
     * Sends a notification message to the IO Notification Queue.
     *
     * @param notificationQueueDTO the notification data to be sent.
     */
    void sendMessage(NotificationQueueDTO notificationQueueDTO);
}
