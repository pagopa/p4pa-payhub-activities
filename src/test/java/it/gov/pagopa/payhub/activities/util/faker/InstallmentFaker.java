package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPersonDTO;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.buildTransferDTO;

public class InstallmentFaker {

    public static InstallmentDTO buildInstallmentDTO(){
        List<TransferDTO> transfers = new ArrayList<>();
        transfers.add(buildTransferDTO());
        return InstallmentDTO.builder()
                .installmentId(1L)
                .status("status")
                .iud("iud")
                .iuv("iuv")
                .iur("iur")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .dueDate(OFFSETDATETIME)
                .paymentTypeCode("paymentTypeCode")
                .amountCents(100L)
                .notificationFeeCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .humanFriendlyRemittanceInformation("humanFriendlyRemittanceInformation")
                .balance("balance")
                .transfers(transfers)
                .debtor(buildPersonDTO())
                .build();
    }
}
