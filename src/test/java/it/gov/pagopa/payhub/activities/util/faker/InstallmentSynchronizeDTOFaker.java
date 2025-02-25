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
                .paymentOptionIndex(1)
                .paymentOptionDescription("paymentOptionDescription")
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
                .numberBeneficiary(5)
                .additionalTransfers(List.of(
                        buildTransferSynchronizeDTO(2),
                        buildTransferSynchronizeDTO(3),
                        buildTransferSynchronizeDTO(4),
                        buildTransferSynchronizeDTO(5)
                ))
                .build();
    }

    private static TransferSynchronizeDTO buildTransferSynchronizeDTO(int index) {
        return TransferSynchronizeDTO.builder()
                .orgFiscalCode("orgFiscalCode_" + index)
                .orgName("orgName_" + index)
                .iban("iban_" + index)
                .remittanceInformation("remittanceInformation_" + index)
                .amount(BigDecimal.valueOf(1))
                .category("category_" + index)
                .transferIndex(index)
                .build();
    }
}
