package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;

public class TransferFaker {

    public static TransferDTO buildTransferDTO(){
        return TransferDTO.builder()
                .transferId(1L)
                .orgFiscalCode("orgFiscalCode")
                .orgName("beneficiaryName")
                .iban("iban")
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .stampType("stampType")
                .category("category")
                .transferIndex(1)
                .build();
    }
}
