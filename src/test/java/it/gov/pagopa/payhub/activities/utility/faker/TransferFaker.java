package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;

import static it.gov.pagopa.payhub.activities.utility.TestUtils.DATE;

public class TransferFaker {

    public static TransferDTO buildTransferDTO(){
        return TransferDTO.builder()
                .transferId(1L)
                .orgFiscalCode("orgFiscalCode")
                .beneficiaryName("beneficiaryName")
                .iban("iban")
                .amount(100L)
                .creationDate(DATE.toInstant())
                .lastUpdateDate(DATE.toInstant())
                .remittanceInformation("remittanceInformation")
                .stampType("stampType")
                .category("category")
                .documentHash("documentHash")
                .provincialResidence("provincialResidence")
                .transferIndex(1)
                .build();
    }
}
