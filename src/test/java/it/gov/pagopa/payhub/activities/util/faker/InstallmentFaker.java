package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
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
        return TestUtils.getPodamFactory().manufacturePojo(InstallmentDTO.class)
                .installmentId(1L)
                .paymentOptionId(1L)
                .status(InstallmentDTO.StatusEnum.PAID)
                .iupdPagopa("iupdPagopa")
                .iud("iud")
                .iuv("iuv")
                .iur("iur")
                .iuf("iuf")
                .nav("nav")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .dueDate(OFFSETDATETIME)
                .paymentTypeCode("paymentTypeCode")
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .balance("balance")
                .transfers(transfers)
                .debtor(buildPersonDTO());
    }

}
