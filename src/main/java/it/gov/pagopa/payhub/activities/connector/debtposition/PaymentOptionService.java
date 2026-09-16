package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionStatus;

public interface PaymentOptionService {

    void updateStatus(Long paymentOptionId, PaymentOptionStatus status);
}
