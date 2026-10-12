package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class PaymentsClient {

    private final SendApisHolder sendApisHolder;

    public PaymentsClient(SendApisHolder sendApisHolder) {
        this.sendApisHolder = sendApisHolder;
    }

    public SendNotificationDTO notifyPayment(Long organizationId, String nav, String accessToken) {
        return sendApisHolder.getPaymentsApi(accessToken)
                .notifyPayment(organizationId, nav);
    }
}
