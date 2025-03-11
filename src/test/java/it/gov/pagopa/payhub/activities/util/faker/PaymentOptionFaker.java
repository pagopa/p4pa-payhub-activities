package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO2;

public class PaymentOptionFaker {

    public static PaymentOptionDTO buildPaymentOptionDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(PaymentOptionDTO.class)
                .debtPositionId(1L)
                .paymentOptionId(1L)
                .totalAmountCents(100L)
                .status(PaymentOptionDTO.StatusEnum.PAID)
                .dueDate(TestUtils.LOCALDATE)
                .installments(List.of(buildInstallmentDTO()))
                .description("description")
                .paymentOptionType(PaymentOptionDTO.PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
    }

    public static PaymentOptionDTO buildPaymentOptionDTO2(){
        return TestUtils.getPodamFactory().manufacturePojo(PaymentOptionDTO.class)
                .debtPositionId(1L)
                .paymentOptionId(2L)
                .totalAmountCents(100L)
                .status(PaymentOptionDTO.StatusEnum.UNPAID)
                .dueDate(TestUtils.LOCALDATE)
                .installments(List.of(buildInstallmentDTO(), buildInstallmentDTO2()))
                .description("description")
                .paymentOptionType(PaymentOptionDTO.PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
    }

}
