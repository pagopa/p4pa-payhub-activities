package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;

import java.util.Map;


/**
 * This interface defines the activity for sending debt position notification messages
 * to the IO Notification service.
 */
@ActivityInterface
public interface SendDebtPositionIONotificationActivity {

    /**
     * Sends a notification message for the specified debt position to the IO Notification service.
     *
     * @param requestedDebtPosition the {@link DebtPositionDTO} containing the details of the debt position to be notified (as given to the WF).
     * @param iupdSyncStatusUpdateDTOMap the map of the correctly synchronized IUD
     * @return the {@link MessageResponseDTO} that contains the notification id
     */
    @ActivityMethod
    MessageResponseDTO sendMessage(DebtPositionDTO requestedDebtPosition, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap);
}



