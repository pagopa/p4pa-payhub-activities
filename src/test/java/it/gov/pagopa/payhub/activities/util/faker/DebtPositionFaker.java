package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionStatus;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO2;

public class DebtPositionFaker {

    public static DebtPositionDTO buildDebtPositionDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(DebtPositionDTO.class)
                .status(DebtPositionStatus.PAID)
                .flagPagoPaPayment(true)
                .paymentOptions(List.of(buildPaymentOptionDTO()));
    }

    public static DebtPositionDTO buildDebtPositionDTOWithMultiplePO(){
        return TestUtils.getPodamFactory().manufacturePojo(DebtPositionDTO.class)
                .paymentOptions(List.of(buildPaymentOptionDTO(), buildPaymentOptionDTO2()));
    }

}
