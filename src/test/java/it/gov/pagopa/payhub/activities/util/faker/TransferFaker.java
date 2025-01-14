package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;

public class TransferFaker {

    public static TransferDTO buildTransferDTO(){
        return TransferDTO.builder()
                .transferId(1L)
                .orgFiscalCode("orgFiscalCode")
                .orgName("beneficiaryName")
                .iban("iban")
                .postalIban("postalIban")
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .stampType("stampType")
                .stampHashDocument("stampHashDocument")
                .stampProvincialResidence("stampProvincialResidence")
                .category("category")
                .transferIndex(1L)
                .build();
    }

    public static it.gov.pagopa.pu.pagopapayments.dto.generated.TransferDTO buildPaymentsTransferDTO(){
        return it.gov.pagopa.pu.pagopapayments.dto.generated.TransferDTO.builder()
                .transferId(1L)
                .orgFiscalCode("orgFiscalCode")
                .orgName("beneficiaryName")
                .iban("iban")
                .postalIban("postalIban")
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .stampType("stampType")
                .stampHashDocument("stampHashDocument")
                .stampProvincialResidence("stampProvincialResidence")
                .category("category")
                .transferIndex(1)
                .build();
    }
}
