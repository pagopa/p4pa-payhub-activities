package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class PaymentsClient {

    private final SendApisHolder sendApisHolder;

    public PaymentsClient(SendApisHolder sendApisHolder) {
        this.sendApisHolder = sendApisHolder;
    }

    public SendNotificationDTO notifyPayment(Long organizationId, String nav, String accessToken) {
        try {
            return sendApisHolder.getPaymentsApi(accessToken)
                    .notifyPayment(organizationId, nav);
        } catch (RestInvokeNotFoundException e) {
            log.info("Cannot find SendNotification having orgId {} and nav {}", organizationId, nav);
            return null;
        }
    }
}
