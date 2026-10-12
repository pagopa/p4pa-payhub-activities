package it.gov.pagopa.payhub.activities.activity.sendnotification.payments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

@ActivityInterface
public interface NotifyPaymentActivity {
    @ActivityMethod
    SendNotificationDTO notifyPayment(DebtPositionDTO debtPosition, Long receiptId);
}
