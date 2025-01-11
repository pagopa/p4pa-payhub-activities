package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;


/**
 * This interface defines the activity for sending debt position notification messages
 * to the IO Notification service.
 */
@ActivityInterface
public interface SendDebtPositionIONotificationActivity {

    /**
     * Sends a notification message for the specified debt position to the IO Notification service.
     *
     * @param debtPosition the {@link DebtPositionDTO} containing the details of the debt position to be notified.
     */
    @ActivityMethod
    void sendMessage(DebtPositionDTO debtPosition);
}



