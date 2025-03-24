package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSyncStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
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
                .status(InstallmentStatus.PAID)
                .iupdPagopa("iupdPagopa")
                .iud("iud")
                .iuv("iuv")
                .iur("iur")
                .iuf("iuf")
                .nav("nav")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .dueDate(LOCALDATE)
                .syncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.DRAFT).syncStatusTo(InstallmentStatus.UNPAID).build())
                .notificationFeeCents(1000L)
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .balance("balance")
                .transfers(transfers)
                .debtor(buildPersonDTO());
    }

    public static InstallmentDTO buildInstallmentDTO2(){
        List<TransferDTO> transfers = new ArrayList<>();
        transfers.add(buildTransferDTO());
        return TestUtils.getPodamFactory().manufacturePojo(InstallmentDTO.class)
                .installmentId(2L)
                .paymentOptionId(2L)
                .status(InstallmentStatus.UNPAID)
                .iupdPagopa("iupdPagopa")
                .iud("iud2")
                .iuv("iuv")
                .iur("iur")
                .iuf("iuf")
                .nav("nav")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .dueDate(LOCALDATE)
                .notificationFeeCents(1000L)
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .balance("balance")
                .transfers(transfers)
                .debtor(buildPersonDTO());
    }

}
