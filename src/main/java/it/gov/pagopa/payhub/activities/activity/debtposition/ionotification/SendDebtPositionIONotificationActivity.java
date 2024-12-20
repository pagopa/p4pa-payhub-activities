package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;


/**
 * This interface defines the activity for sending debt position notification messages
 * to the IO Notification service.
 */
public interface SendDebtPositionIONotificationActivity {

    /**
     * Sends a notification message for the specified debt position to the IO Notification service.
     *
     * @param debtPosition the {@link DebtPositionDTO} containing the details of the debt position to be notified.
     */
    void sendMessage(DebtPositionDTO debtPosition);
}



