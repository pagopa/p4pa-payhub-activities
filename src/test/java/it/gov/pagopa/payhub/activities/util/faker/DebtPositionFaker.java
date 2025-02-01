package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentsPaymentOptionDTO;

public class DebtPositionFaker {

    public static DebtPositionDTO buildDebtPositionDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(DebtPositionDTO.class)
                .paymentOptions(List.of(buildPaymentOptionDTO()));
    }

    public static it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO buildPaymentsDebtPositionDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO.class)
                .paymentOptions(List.of(buildPaymentsPaymentOptionDTO()));
    }
}
