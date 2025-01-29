package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.Stamp;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

public class TransferFaker {

    public static Transfer buildTransfer(){
        return Transfer.builder()
                .transferId(1L)
                .orgFiscalCode("orgFiscalCode")
                .orgName("beneficiaryName")
                .iban("iban")
                .postalIban("postalIban")
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .stamp(Stamp.builder()
                    .stampType("stampType")
                    .stampHashDocument("stampHashDocument")
                    .stampProvincialResidence("stampProvincialResidence")
                    .build()
                )
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

    public static it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO buildTransferDTO(){
        return it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO.builder()
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
}
