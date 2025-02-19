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
                .flagMultiBeneficiary(true)
                .numberBeneficiary(3)
                .orgFiscalCode_2("orgFiscalCode_2")
                .orgName_2("orgName_2")
                .iban_2("iban_2")
                .orgRemittanceInformation_2("orgRemittanceInformation_2")
                .amount_2(BigDecimal.valueOf(1))
                .category_2("category_2")
                .orgFiscalCode_3("orgFiscalCode_3")
                .orgName_3("orgName_3")
                .iban_3("iban_3")
                .orgRemittanceInformation_3("orgRemittanceInformation_3")
                .amount_3(BigDecimal.valueOf(1))
                .category_3("category_3")
                .build();
    }

    static class TransferFake extends InstallmentIngestionFlowFileDTO {
        @Override
        public String getOrgFiscalCode_2() {
            throw new NoSuchMethodError("Error");
        }
    }

    public static InstallmentIngestionFlowFileDTO buildTransferFake() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = new TransferFake();
        installmentIngestionFlowFileDTO.setIngestionFlowFileLineNumber(1L);
        installmentIngestionFlowFileDTO.setAction(InstallmentIngestionFlowFileDTO.ActionEnum.I);
        installmentIngestionFlowFileDTO.setDescription("description");
        installmentIngestionFlowFileDTO.setAmount(BigDecimal.valueOf(1));
        installmentIngestionFlowFileDTO.setDebtPositionTypeCode("typeCode");
        installmentIngestionFlowFileDTO.setEntityType(InstallmentIngestionFlowFileDTO.EntityTypeEnum.F);
        installmentIngestionFlowFileDTO.setIupdOrg("iupd");
        installmentIngestionFlowFileDTO.setRemittanceInformation("info");
        installmentIngestionFlowFileDTO.setPaymentOptionIndex(1);
        installmentIngestionFlowFileDTO.setPaymentOptionType("type");
        installmentIngestionFlowFileDTO.setPaymentTypeCode("code");
        installmentIngestionFlowFileDTO.setIud("iud");
        installmentIngestionFlowFileDTO.setFiscalCode("fiscalCode");
        installmentIngestionFlowFileDTO.setLegacyPaymentMetadata("legacy");
        installmentIngestionFlowFileDTO.setFullName("name");
        installmentIngestionFlowFileDTO.setNumberBeneficiary(2);
        installmentIngestionFlowFileDTO.setFlagMultiBeneficiary(true);
        installmentIngestionFlowFileDTO.setOrgFiscalCode_2(null);
        installmentIngestionFlowFileDTO.setOrgName_2("orgName_2");
        installmentIngestionFlowFileDTO.setAmount_2(BigDecimal.valueOf(1));
        installmentIngestionFlowFileDTO.setOrgRemittanceInformation_2("remittanceInformation_2");
        installmentIngestionFlowFileDTO.setIban_2("iban_2");
        installmentIngestionFlowFileDTO.setCategory_2("category_2");
        return installmentIngestionFlowFileDTO;
    }
}
