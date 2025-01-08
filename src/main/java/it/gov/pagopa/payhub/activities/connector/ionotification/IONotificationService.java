package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;


/**
 * This interface provides a method for sending notification messages to the microservice IO Notification.
 */
public interface IONotificationService {

    /**
     * Sends a notification message to the IO Notification Queue.
     *
     * @param debtPositionDTO the debt position data to be sent.
     */
    void sendMessage(DebtPositionDTO debtPositionDTO);
}
