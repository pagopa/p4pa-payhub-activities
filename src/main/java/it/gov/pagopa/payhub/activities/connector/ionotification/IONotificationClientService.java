package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;


/**
 * This interface provides a method for sending notification messages to the microservice IO Notification.
 */
public interface IONotificationClientService {

    /**
     * Sends a notification message to the IO Notification.
     *
     * @param notificationRequestDTO the payload data to be sent.
     * @return the object {@link MessageResponseDTO} with the notification id.
     */
    MessageResponseDTO sendMessage(NotificationRequestDTO notificationRequestDTO);
}
