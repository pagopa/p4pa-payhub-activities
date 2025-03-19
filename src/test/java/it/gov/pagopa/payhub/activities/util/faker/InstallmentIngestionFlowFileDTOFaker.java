package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.math.BigDecimal;

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;

public class InstallmentIngestionFlowFileDTOFaker {

    public static InstallmentIngestionFlowFileDTO buildInstallmentIngestionFlowFileDTO() {
        return InstallmentIngestionFlowFileDTO.builder()
                .action(InstallmentIngestionFlowFileDTO.ActionEnum.I)
                .draft(false)
                .iupdOrg("iupd")
                .description("description")
                .validityDate(LOCALDATE)
                .multiDebtor(false)
                .notificationDate(LOCALDATE)
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
                .dueDate(LOCALDATE)
                .amount(BigDecimal.valueOf(1L))
                .debtPositionTypeCode("debtPositionTypeCode")
                .notificationFee(BigDecimal.valueOf(10))
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .flagPagoPaPayment(false)
                .balance("balance")
                .flagMultiBeneficiary(true)
                .numberBeneficiary(5)
                .transfer2(buildTransferData(2))
                .transfer3(buildTransferData(3))
                .transfer4(buildTransferData(4))
                .transfer5(buildTransferData(5))
                .executionConfig("executionConfig")
                .build();
    }

    public static InstallmentIngestionFlowFileDTO buildTransferFake() {
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = new TransferFake();
        installmentIngestionFlowFileDTO.setAction(InstallmentIngestionFlowFileDTO.ActionEnum.I);
        installmentIngestionFlowFileDTO.setDescription("description");
        installmentIngestionFlowFileDTO.setAmount(BigDecimal.valueOf(1L));
        installmentIngestionFlowFileDTO.setDebtPositionTypeCode("typeCode");
        installmentIngestionFlowFileDTO.setEntityType(InstallmentIngestionFlowFileDTO.EntityTypeEnum.F);
        installmentIngestionFlowFileDTO.setIupdOrg("iupd");
        installmentIngestionFlowFileDTO.setRemittanceInformation("info");
        installmentIngestionFlowFileDTO.setPaymentOptionIndex(1);
        installmentIngestionFlowFileDTO.setPaymentOptionType("type");
        installmentIngestionFlowFileDTO.setNotificationFee(BigDecimal.valueOf(10));
        installmentIngestionFlowFileDTO.setIud("iud");
        installmentIngestionFlowFileDTO.setFiscalCode("fiscalCode");
        installmentIngestionFlowFileDTO.setLegacyPaymentMetadata("legacy");
        installmentIngestionFlowFileDTO.setFullName("name");
        installmentIngestionFlowFileDTO.setNumberBeneficiary(2);
        installmentIngestionFlowFileDTO.setFlagMultiBeneficiary(true);
        installmentIngestionFlowFileDTO.setFlagPagoPaPayment(true);
        installmentIngestionFlowFileDTO.setTransfer2(buildTransferData(2));
        return installmentIngestionFlowFileDTO;
    }

    private static MultiValuedMap<String, String> buildTransferData(int index) {
        MultiValuedMap<String, String> transferData = new ArrayListValuedHashMap<>();

        transferData.put("codiceFiscaleEnte_" + index, "codiceFiscaleEnte");
        transferData.put("denominazioneEnte_" + index, "denominazioneEnte");
        transferData.put("ibanAccreditoEnte_" + index, "ibanAccreditoEnte");
        transferData.put("causaleVersamentoEnte_" + index, "causaleVersamentoEnte");
        transferData.put("importoVersamentoEnte_" + index, BigDecimal.valueOf(1).toString());
        transferData.put("codiceTassonomiaEnte_" + index, "codiceTassonomiaEnte");

        return transferData;
    }

    static class TransferFake extends InstallmentIngestionFlowFileDTO {
        @Override
        public MultiValuedMap<String, String> getTransfer2() {
            MultiValuedMap<String, String> fakeTransfer2 = new ArrayListValuedHashMap<>();

            fakeTransfer2.put("codiceFiscaleEnte_2", "codiceFiscaleEnte");
            fakeTransfer2.put("denominazioneEnte_2", "denominazioneEnte");
            fakeTransfer2.put("ibanAccreditoEnte_2", "ibanAccreditoEnte");
            fakeTransfer2.put("importoVersamentoEnte_2", Long.toString(1L));
            fakeTransfer2.put("codiceTassonomiaEnte_2", "codiceTassonomiaEnte");

            return fakeTransfer2;
        }
    }
}
