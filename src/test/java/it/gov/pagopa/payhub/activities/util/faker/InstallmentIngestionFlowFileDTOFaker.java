package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.math.BigDecimal;
import java.util.stream.IntStream;

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
                .transfers(buildTransferData(3))
                .build();
    }

    static class TransferFake extends InstallmentIngestionFlowFileDTO {
        @Override
        public MultiValuedMap<String, String> getTransfers() {
            MultiValuedMap<String, String> fakeTransfers = new ArrayListValuedHashMap<>();

            fakeTransfers.put("orgName_2", "orgName_2");
            fakeTransfers.put("iban_2", "iban_2");
            fakeTransfers.put("orgRemittanceInformation_2", "remittanceInformation_2");
            fakeTransfers.put("amount_2", BigDecimal.valueOf(1).toString());
            fakeTransfers.put("category_2", "category_2");

            return fakeTransfers;
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
        installmentIngestionFlowFileDTO.setTransfers(buildTransferData(2));
        return installmentIngestionFlowFileDTO;
    }

    private static MultiValuedMap<String, String> buildTransferData(int numberOfBeneficiaries) {
        MultiValuedMap<String, String> transfers = new ArrayListValuedHashMap<>();

        IntStream.rangeClosed(2, numberOfBeneficiaries)
                .forEach(i -> {
                    transfers.put("orgFiscalCode_" + i, "orgFiscalCode_" + i);
                    transfers.put("orgName_" + i, "orgName_" + i);
                    transfers.put("iban_" + i, "iban_" + i);
                    transfers.put("orgRemittanceInformation_" + i, "remittanceInformation_" + i);
                    transfers.put("amount_" + i, BigDecimal.valueOf(1).toString());
                    transfers.put("category_" + i, "category_" + i);
                });

        return transfers;
    }

}
