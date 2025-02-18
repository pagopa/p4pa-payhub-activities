package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;

import java.math.BigDecimal;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;

public class InstallmentIngestionFlowFileDTOFaker {

    public static InstallmentIngestionFlowFileDTO buildInstallmentIngestionFlowFileDTO() {
        return InstallmentIngestionFlowFileDTO.builder()
                .ingestionFlowFileLineNumber(1L)
                .action(InstallmentIngestionFlowFileDTO.ActionEnum.I)
                .draft(false)
                .iupdOrg("iupd")
                .description("description")
                .validityDate(OFFSETDATETIME)
                .multiDebtor(false)
                .notificationDate(OFFSETDATETIME)
                .paymentOptionIndex(1)
                .paymentOptionType("paymentOptionType")
                .paymentOptionDescription("paymentOptionDescription")
                .iud("iud")
                .iuv("iuv")
                .entityType(InstallmentIngestionFlowFileDTO.EntityTypeEnum.F)
                .fiscalCode("fiscalCode")
                .fullName("fullName")
                .address("address")
                .civic("civic")
                .postalCode("postalCode")
                .location("location")
                .province("province")
                .nation("nation")
                .email("email")
                .dueDate(OFFSETDATETIME)
                .amount(BigDecimal.valueOf(1))
                .debtPositionTypeCode("debtPositionTypeCode")
                .paymentTypeCode("paymentTypeCode")
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .flagPagoPaPayment(false)
                .balance("balance")
                .flagMultiBeneficiary(false)
                .numberBeneficiary(1)
                .orgFiscalCode_2("orgFiscalCode_2")
                .orgName_2("orgName_2")
                .iban_2("iban_2")
                .orgAddress_2("orgAddress_2")
                .orgCivic_2("orgCivic_2")
                .orgPostCode_2("orgPostCode_2")
                .orgCity_2("orgCity_2")
                .orgProvince_2("orgProvince_2")
                .orgNation_2("orgNation_2")
                .orgRemittanceInformation_2("orgRemittanceInformation_2")
                .amount_2(BigDecimal.valueOf(1))
                .category_2("category_2")
                .orgFiscalCode_3("orgFiscalCode_3")
                .orgName_3("orgName_3")
                .iban_3("iban_3")
                .amount_3(BigDecimal.valueOf(1))
                .category_3("category_3")
                .build();
    }
}
