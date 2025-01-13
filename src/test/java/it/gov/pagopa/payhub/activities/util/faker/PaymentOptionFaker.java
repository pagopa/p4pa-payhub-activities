package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;

public class PaymentOptionFaker {

    public static PaymentOptionDTO buildPaymentOptionDTO(){
        return PaymentOptionDTO.builder()
                .totalAmountCents(100L)
                .status("status")
                .dueDate(TestUtils.OFFSETDATETIME)
                .installments(List.of(buildInstallmentDTO()))
                .multiDebtor(false)
                .description("description")
                .paymentOptionType(PaymentOptionDTO.PaymentOptionTypeEnum.SINGLE_INSTALLMENT)
                .build();
    }
}
