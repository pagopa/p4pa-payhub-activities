package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferSynchronizeDTO;

import java.math.BigDecimal;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;

public class InstallmentSynchronizeDTOFaker {

    public static InstallmentSynchronizeDTO buildInstallmentSynchronizeDTO() {
        return InstallmentSynchronizeDTO.builder()
                .organizationId(1L)
                .ingestionFlowFileId(1L)
                .ingestionFlowFileLineNumber(1L)
                .action(InstallmentSynchronizeDTO.ActionEnum.I)
                .draft(false)
                .iupdOrg("iupd")
                .description("description")
                .validityDate(OFFSETDATETIME)
                .multiDebtor(false)
                .notificationDate(OFFSETDATETIME)
                .paymentOptionIndex(1L)
                .paymentOptionType("paymentOptionType")
                .iud("iud")
                .iuv("iuv")
                .entityType(InstallmentSynchronizeDTO.EntityTypeEnum.F)
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
                .flagMultibeneficiary(true)
                .numberBeneficiary(3L)
                .transfersList(List.of(buildTransferSynchronizeDTO2(), buildTransferSynchronizeDTO3()))
                .build();
    }

    private static TransferSynchronizeDTO buildTransferSynchronizeDTO2(){
        return TransferSynchronizeDTO.builder()
                .orgFiscalCode("orgFiscalCode_2")
                .orgName("orgName_2")
                .iban("iban_2")
                .remittanceInformation("remittanceInformation_2")
                .amount(BigDecimal.valueOf(1))
                .category("category_2")
                .transferIndex(2)
                .build();
    }

    private static TransferSynchronizeDTO buildTransferSynchronizeDTO3(){
        return TransferSynchronizeDTO.builder()
                .orgFiscalCode("orgFiscalCode_3")
                .orgName("orgName_3")
                .iban("iban_3")
                .remittanceInformation("remittanceInformation_3")
                .amount(BigDecimal.valueOf(1))
                .category("category_3")
                .transferIndex(3)
                .build();
    }
}
