package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionStatus;

public interface PaymentOptionService {

    void updateStatus(Long paymentOptionId, PaymentOptionStatus status);
}
