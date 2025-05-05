package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIoNotificationDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.GenericWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncCompleteDTO;

import java.util.Map;


/**
 * This interface defines the activity for sending debt position notification messages
 * to the IO Notification service.
 */
@ActivityInterface
public interface IONotificationDebtPositionActivity {

    /**
     * Sends a notification message for the specified debt position to the IO Notification service.
     *
     * @param requestedDebtPosition the {@link DebtPositionDTO} containing the details of the debt position to be notified (as given to the WF).
     * @param iupdSyncStatusUpdateDTOMap the map of the correctly synchronized IUD
     * @param ioMessages IO messages to send customized for the particular DebtPosition provided
     * @return {@link DebtPositionIoNotificationDTO} containing the notification IDs
     */
    @ActivityMethod
    DebtPositionIoNotificationDTO sendIoNotification(DebtPositionDTO requestedDebtPosition, Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap, GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages);
}



