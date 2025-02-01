package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentStatus;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPaymentsPersonDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPersonDTO;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.buildPaymentsTransferDTO;
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
                .notificationFeeCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .humanFriendlyRemittanceInformation("humanFriendlyRemittanceInformation")
                .balance("balance")
                .transfers(transfers)
                .debtor(buildPersonDTO());
    }

    public static it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentDTO buildPaymentsInstallmentDTO(){
        List<it.gov.pagopa.pu.pagopapayments.dto.generated.TransferDTO> transfers = new ArrayList<>();
        transfers.add(buildPaymentsTransferDTO());
        return TestUtils.getPodamFactory().manufacturePojo(it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentDTO.class)
                .installmentId(1L)
                .paymentOptionId(1L)
                .status(InstallmentStatus.PAID)
                .debtPositionOrigin(DebtPositionOrigin.ORDINARY)
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
                .notificationFeeCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .humanFriendlyRemittanceInformation("humanFriendlyRemittanceInformation")
                .balance("balance")
                .debtor(buildPaymentsPersonDTO())
                .transfers(transfers);
    }
}
