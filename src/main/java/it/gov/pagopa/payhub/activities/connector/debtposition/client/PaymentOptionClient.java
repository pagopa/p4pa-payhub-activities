package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class PaymentOptionClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public PaymentOptionClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public void updateStatus(Long paymentOptionId, PaymentOptionStatus status, String accessToken){
        log.info("Update status for paymentOptionId: {}", paymentOptionId);
        debtPositionApisHolder.getPaymentOptionSearchControllerApi(accessToken).crudPaymentOptionsUpdateStatus(paymentOptionId, status);
    }
}
