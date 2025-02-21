package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

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
                .transfer2(buildTransferData(2))
                .transfer3(buildTransferData(3))
                .build();
    }

    static class TransferFake extends InstallmentIngestionFlowFileDTO {
        @Override
        public MultiValuedMap<String, String> getTransfer2() {
            MultiValuedMap<String, String> fakeTransfer2 = new ArrayListValuedHashMap<>();

            fakeTransfer2.put("orgName", "orgName_2");
            fakeTransfer2.put("iban", "iban_2");
            fakeTransfer2.put("orgRemittanceInformation", "remittanceInformation_2");
            fakeTransfer2.put("amount", BigDecimal.valueOf(1).toString());
            fakeTransfer2.put("category", "category_2");

            return fakeTransfer2;
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
        installmentIngestionFlowFileDTO.setTransfer2(buildTransferData(2));
        return installmentIngestionFlowFileDTO;
    }

    private static MultiValuedMap<String, String> buildTransferData(int index) {
        MultiValuedMap<String, String> transferData = new ArrayListValuedHashMap<>();

        transferData.put("orgFiscalCode", "orgFiscalCode_" + index);
        transferData.put("orgName", "orgName_" + index);
        transferData.put("iban", "iban_" + index);
        transferData.put("orgRemittanceInformation", "remittanceInformation_" + index);
        transferData.put("amount", BigDecimal.valueOf(1).toString());
        transferData.put("category", "category_" + index);

        return transferData;
    }

}
