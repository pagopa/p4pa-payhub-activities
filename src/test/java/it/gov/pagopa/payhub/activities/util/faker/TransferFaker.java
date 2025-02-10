package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.Stamp;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;

public class TransferFaker {

    public static Transfer buildTransfer(){
        return TestUtils.getPodamFactory().manufacturePojo(Transfer.class)
                .transferId(1L)
                .installmentId(1L)
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
                .transferIndex(1);
    }


    public static it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO buildTransferDTO(){
        return TestUtils.getPodamFactory().manufacturePojo(it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO.class)
            .transferId(1L)
            .installmentId(1L)
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
            .transferIndex(1);
    }
}
