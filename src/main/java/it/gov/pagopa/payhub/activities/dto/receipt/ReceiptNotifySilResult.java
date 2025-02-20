package it.gov.pagopa.payhub.activities.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the ReceiptSilNotifyResult, representing the result of receipt notification to sil.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptNotifySilResult {
    /** whether the notification is to send */
    private boolean notificationToSend;
    /** outcome */
    private boolean success;
}
