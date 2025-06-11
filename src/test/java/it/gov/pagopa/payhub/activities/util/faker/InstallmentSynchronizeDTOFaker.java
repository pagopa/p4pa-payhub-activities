package it.gov.pagopa.payhub.activities.util.faker;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.gov.pagopa.pu.debtposition.dto.generated.Action;
import it.gov.pagopa.pu.debtposition.dto.generated.EntityTypeEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferSynchronizeDTO;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIMEENDOFTHEDAY;

public class InstallmentSynchronizeDTOFaker {

    public static InstallmentSynchronizeDTO buildInstallmentSynchronizeDTO() {
        return InstallmentSynchronizeDTO.builder()
                .organizationId(1L)
                .ingestionFlowFileId(1L)
                .ingestionFlowFileName("fileName.zip")
                .ingestionFlowFileLineNumber(1L)
                .action(Action.I)
                .draft(false)
                .iupdOrg("iupd")
                .description("description")
                .validityDate(LOCALDATE)
                .multiDebtor(false)
                .notificationDate(OFFSETDATETIMEENDOFTHEDAY)
                .paymentOptionIndex(1)
                .paymentOptionDescription("paymentOptionDescription")
                .paymentOptionType("paymentOptionType")
                .iud("iud")
                .iuv("iuv")
                .entityType(EntityTypeEnum.F)
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
                .amountCents(100L)
                .debtPositionTypeCode("debtPositionTypeCode")
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .flagPuPagoPaPayment(false)
                .balance("balance")
                .flagMultibeneficiary(true)
                .numberBeneficiary(5)
                .additionalTransfers(List.of(
                        buildTransferSynchronizeDTO(2),
                        buildTransferSynchronizeDTO(3),
                        buildTransferSynchronizeDTO(4),
                        buildTransferSynchronizeDTO(5)
                ))
                .executionConfig(JsonNodeFactory.instance.objectNode().put("executionConfig", "test"))
                .ingestionFlowFileName("fileName.zip")
                .build();
    }

    private static TransferSynchronizeDTO buildTransferSynchronizeDTO(int index) {
        return TransferSynchronizeDTO.builder()
                .orgFiscalCode("codiceFiscaleEnte_" + index)
                .orgName("denominazioneEnte_" + index)
                .iban("ibanAccreditoEnte_" + index)
                .remittanceInformation("causaleVersamentoEnte_" + index)
                .amountCents(100L)
                .category("codiceTassonomiaEnte_" + index)
                .transferIndex(index)
                .build();
    }
}
