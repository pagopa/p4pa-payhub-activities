package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildPaymentsInstallmentDTO;

public class PaymentOptionFaker {

    public static PaymentOptionDTO buildPaymentOptionDTO(){
        return PaymentOptionDTO.builder()
                .paymentOptionId(1L)
                .totalAmountCents(100L)
                .status(PaymentOptionDTO.StatusEnum.valueOf("status"))
                .dueDate(TestUtils.OFFSETDATETIME)
                .installments(List.of(buildInstallmentDTO()))
                .multiDebtor(false)
                .description("description")
                .paymentOptionType(PaymentOptionDTO.PaymentOptionTypeEnum.SINGLE_INSTALLMENT)
                .build();
    }

    public static it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentOptionDTO buildPaymentsPaymentOptionDTO(){
        return it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentOptionDTO.builder()
                .paymentOptionId(1L)
                .totalAmountCents(100L)
                .status("status")
                .dueDate(TestUtils.OFFSETDATETIME)
                .installments(List.of(buildPaymentsInstallmentDTO()))
                .multiDebtor(false)
                .description("description")
                .paymentOptionType(it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentOptionDTO.PaymentOptionTypeEnum.SINGLE_INSTALLMENT)
                .build();
    }
}
