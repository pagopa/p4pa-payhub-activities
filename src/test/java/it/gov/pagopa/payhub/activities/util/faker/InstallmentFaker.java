package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.DATE;
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
                .creationDate(DATE.toInstant())
                .updateDate(DATE.toInstant())
                .dueDate(LocalDate.of(2099, 5, 15))
                .paymentTypeCode("paymentTypeCode")
                .amount(100L)
                .fee(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .iuvCreationDate(DATE.toInstant())
                .humanFriendlyRemittanceInformation("humanFriendlyRemittanceInformation")
                .balance("balance")
                .flagGenerateIuv(true)
                .sessionId("sessionId")
                .flagIuvVolatile(true)
                .transfers(transfers)
                .payer(buildPersonDTO())
                .build();
    }
}
