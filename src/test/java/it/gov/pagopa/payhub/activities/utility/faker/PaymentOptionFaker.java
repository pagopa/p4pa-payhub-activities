package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.debtposition.PaymentOptionDTO;

import java.time.LocalDate;
import java.util.List;

import static it.gov.pagopa.payhub.activities.utility.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.OrganizationFaker.buildOrganizationDTO;

public class PaymentOptionFaker {

    public static PaymentOptionDTO buildPaymentOptionDTO(){
        return PaymentOptionDTO.builder()
                .org(buildOrganizationDTO())
                .totalAmount(100L)
                .status("status")
                .dueDate(LocalDate.of(2024, 5, 15))
                .installments(List.of(buildInstallmentDTO()))
                .multiDebtor(false)
                .description("description")
                .build();
    }
}
