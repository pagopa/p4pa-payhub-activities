package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.PaymentOptionClient;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class PaymentOptionServiceImpl implements PaymentOptionService{

    private final AuthnService authnService;
    private final PaymentOptionClient paymentOptionClient;

    public PaymentOptionServiceImpl(AuthnService authnService, PaymentOptionClient paymentOptionClient) {
        this.authnService = authnService;
        this.paymentOptionClient = paymentOptionClient;
    }

    @Override
    public void updateStatus(Long paymentOptionId, PaymentOptionStatus status) {
        paymentOptionClient.updateStatus(paymentOptionId, status, authnService.getAccessToken());
    }
}
