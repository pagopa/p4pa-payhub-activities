package it.gov.pagopa.payhub.activities.activity.sendnotification.payments;

import it.gov.pagopa.payhub.activities.connector.sendnotification.PaymentsService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class NotifyPaymentActivityImpl implements NotifyPaymentActivity {

    private final PaymentsService paymentsService;

    public NotifyPaymentActivityImpl(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @Override
    public SendNotificationDTO notifyPayment(DebtPositionDTO debtPosition, Long receiptId) {
        log.info("Notify payment for receiptId {}", receiptId);
        return paymentsService.notifyPayment(debtPosition, receiptId);
    }
}
