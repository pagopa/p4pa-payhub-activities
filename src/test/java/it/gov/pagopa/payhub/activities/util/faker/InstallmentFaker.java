package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.Utilities.longCentsToBigDecimalEuro;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPersonDTO;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.buildTransferDTO;
import static it.gov.pagopa.pu.debtposition.dto.generated.Action.I;

public class InstallmentFaker {

    public static InstallmentDTO buildInstallmentDTO(){
        List<TransferDTO> transfers = new ArrayList<>();
        transfers.add(buildTransferDTO());
        return TestUtils.getPodamFactory().manufacturePojo(InstallmentDTO.class)
                .installmentId(1L)
                .ingestionFlowFileId(1L)
                .paymentOptionId(1L)
                .status(InstallmentStatus.PAID)
                .syncStatus(null)
                .generateNotice(Boolean.TRUE)
                .iupdPagopa("iupdPagopa")
                .iud("iud")
                .iuv("iuv")
                .iur("iur")
                .iuf("iuf")
                .nav("nav")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .dueDate(LOCALDATE)
                .switchToExpired(Boolean.TRUE)
                .notificationFeeCents(1000L)
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .balance("balance")
                .transfers(transfers)
                .ingestionFlowFileAction(I)
                .debtor(buildPersonDTO());
    }

    public static InstallmentDTO buildInstallmentDTO2(){
        List<TransferDTO> transfers = new ArrayList<>();
        transfers.add(buildTransferDTO());
        return TestUtils.getPodamFactory().manufacturePojo(InstallmentDTO.class)
                .installmentId(2L)
                .paymentOptionId(2L)
                .status(InstallmentStatus.UNPAID)
                .syncStatus(null)
                .iupdPagopa("iupdPagopa")
                .iud("iud2")
                .iuv("iuv")
                .iur("iur")
                .iuf("iuf")
                .nav("nav")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .dueDate(LOCALDATE)
                .switchToExpired(Boolean.TRUE)
                .notificationFeeCents(1000L)
                .amountCents(100L)
                .remittanceInformation("remittanceInformation")
                .legacyPaymentMetadata("legacyPaymentMetadata")
                .balance("balance")
                .transfers(transfers)
                .debtor(buildPersonDTO());
    }

    public static CollectionModelInstallmentNoPII buildCollectionModelInstallmentNoPII() {
        List<InstallmentNoPII> items = new ArrayList<>();
        items.add(buildInstallmentNoPII());

        return TestUtils.getPodamFactory().manufacturePojo(CollectionModelInstallmentNoPII.class)
            .embedded(new CollectionModelInstallmentNoPIIEmbedded(items));

    }

    public static InstallmentNoPII buildInstallmentNoPII(){
        return TestUtils.getPodamFactory().manufacturePojo(InstallmentNoPII.class)
            .installmentId(1L)
            .paymentOptionId(1L)
            .status(InstallmentStatus.PAID)
            .syncStatus(null)
            .iupdPagopa("iupdPagopa")
            .iud("iud")
            .iuv("iuv")
            .iur("iur")
            .iuf("iuf")
            .nav("nav")
            .creationDate(OFFSETDATETIME)
            .updateDate(OFFSETDATETIME)
            .dueDate(LOCALDATE)
            .switchToExpired(Boolean.TRUE)
            .notificationFeeCents(1000L)
            .amountCents(100L)
            .remittanceInformation("remittanceInformation")
            .legacyPaymentMetadata("legacyPaymentMetadata")
            .balance("balance");
    }

    public static IUVInstallmentsExportFlowFileDTO buildIUVInstallmentsExportFlowFileDTO(){
        InstallmentDTO dto = buildInstallmentDTO();
        PersonDTO debtor = dto.getDebtor();

        return IUVInstallmentsExportFlowFileDTO.builder()
                .iud(dto.getIud())
                .iuv(dto.getIuv())
                .entityType(debtor.getEntityType())
                .fiscalCode(debtor.getFiscalCode())
                .fullName(debtor.getFullName())
                .address(debtor.getAddress())
                .civic(debtor.getCivic())
                .postalCode(debtor.getPostalCode())
                .location(debtor.getLocation())
                .province(debtor.getProvince())
                .nation(debtor.getNation())
                .email(debtor.getEmail())
                .dueDate(dto.getDueDate())
                .amount(longCentsToBigDecimalEuro(dto.getAmountCents()))
                .paCommission(new BigDecimal(0))
                .debtPositionTypeCode("code")
                .remittanceInformation(dto.getRemittanceInformation())
                .legacyPaymentMetadata(dto.getLegacyPaymentMetadata())
                .balance(dto.getBalance())
                .generateNotice(Boolean.TRUE)
                .action(I)
                .build();
    }

}
