package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;

public interface PaymentsService {

    SendNotificationDTO notifyPayment(DebtPositionDTO debtPosition, Long receiptId);
}
