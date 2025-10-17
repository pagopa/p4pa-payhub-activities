package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.PaidInstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.UniqueIdentifierType;
import it.gov.pagopa.payhub.activities.service.receipt.RtFileHandlerService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonEntityType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Lazy
@Service
public class InstallmentExportFlowFileDTOMapper {

    private static final String ANONYMOUS = "ANONIMO";
    private static final String MARCA_BOLLO = "MARCA_BOLLO";
    private static final String RECEIPT_ATTACHMENT_TYPE = "BD";

    private final RtFileHandlerService rtFileHandlerService;

    public InstallmentExportFlowFileDTOMapper(RtFileHandlerService rtFileHandlerService) {
        this.rtFileHandlerService = rtFileHandlerService;
    }

    public PaidInstallmentExportFlowFileDTO map(InstallmentPaidViewDTO installmentPaidViewDTO) {

        PersonDTO debtor = installmentPaidViewDTO.getDebtor();
        PersonDTO payer = installmentPaidViewDTO.getPayer();

        LocalDate paymentDate = installmentPaidViewDTO.getPaymentDateTime() != null
                ? installmentPaidViewDTO.getPaymentDateTime().toLocalDate()
                : null;

        PaidInstallmentExportFlowFileDTO.PaidInstallmentExportFlowFileDTOBuilder builder = PaidInstallmentExportFlowFileDTO.builder()
                .iuf(installmentPaidViewDTO.getIuf())
                .flowRowNumber(1)
                .iud(installmentPaidViewDTO.getIud())
                .iuv(installmentPaidViewDTO.getNoticeNumber())
                .domainIdentifier(installmentPaidViewDTO.getOrgFiscalCode())
                .receiptMessageIdentifier(installmentPaidViewDTO.getPaymentReceiptId())
                .receiptMessageDateTime(installmentPaidViewDTO.getPaymentDateTime() != null
                        ? installmentPaidViewDTO.getPaymentDateTime().toLocalDateTime()
                        : null)
                .requestMessageReference(installmentPaidViewDTO.getPaymentReceiptId())
                .requestDateReference(paymentDate)
                .uniqueIdentifierType(UniqueIdentifierType.B)
                .uniqueIdentifierCode(installmentPaidViewDTO.getIdPsp())
                .attestingName(installmentPaidViewDTO.getPspCompanyName())
                .beneficiaryEntityType(PersonEntityType.G)
                .beneficiaryUniqueIdentifierCode(installmentPaidViewDTO.getOrgFiscalCode())
                .beneficiaryName(installmentPaidViewDTO.getCompanyName())
                .debtorEntityType(debtor.getEntityType())
                .debtorUniqueIdentifierCode(Objects.requireNonNullElse(debtor.getFiscalCode(), ANONYMOUS))
                .debtorFullName(debtor.getFullName())
                .debtorAddress(debtor.getAddress())
                .debtorStreetNumber(debtor.getCivic())
                .debtorPostalCode(debtor.getPostalCode())
                .debtorCity(debtor.getLocation())
                .debtorProvince(debtor.getProvince())
                .debtorCountry(debtor.getNation())
                .debtorEmail(debtor.getEmail())
                .paymentOutcomeCode(0)
                .totalAmountPaid(Utilities.longCentsToBigDecimalEuro(installmentPaidViewDTO.getPaymentAmountCents()))
                .uniquePaymentIdentifier(installmentPaidViewDTO.getCreditorReferenceId())
                .paymentContextCode(installmentPaidViewDTO.getPaymentReceiptId())
                .singleAmountPaid(Utilities.longCentsToBigDecimalEuro(installmentPaidViewDTO.getAmountCents()))
                .singlePaymentOutcome("0")
                .singlePaymentOutcomeDate(paymentDate)
                .uniqueCollectionIdentifier(installmentPaidViewDTO.getPaymentReceiptId())
                .paymentReason(installmentPaidViewDTO.getRemittanceInformation())
                .collectionSpecificData("9/".concat(installmentPaidViewDTO.getCategory()))
                .dueType(installmentPaidViewDTO.getCode())
                .rt(rtFileHandlerService.read(installmentPaidViewDTO.getOrganizationId(), installmentPaidViewDTO.getRtFilePath()))
                .singlePaymentDataIndex(installmentPaidViewDTO.getTransferIndex())
                .pspAppliedFees(installmentPaidViewDTO.getFeeCents() != null ? Utilities.longCentsToBigDecimalEuro(installmentPaidViewDTO.getFeeCents()) : null)
                .balance(installmentPaidViewDTO.getBalance())
                .orgFiscalCode(installmentPaidViewDTO.getOrgFiscalCode())
                .orgName(installmentPaidViewDTO.getCompanyName())
                .dueTaxonomicCode(installmentPaidViewDTO.getCategory())
                .codIun(installmentPaidViewDTO.getIun())
                .notificationDate(installmentPaidViewDTO.getNotificationDate() != null ? installmentPaidViewDTO.getNotificationDate().toLocalDate() : null)
                .notificationFeeCents(installmentPaidViewDTO.getNotificationFeeCents());

        if (MARCA_BOLLO.equals(installmentPaidViewDTO.getCode())) {
            builder.receiptAttachmentType(RECEIPT_ATTACHMENT_TYPE)
            .receiptAttachmentTest(null); // TODO: field blbRtDatiPagDatiSingPagAllegatoRicevutaTest depends on task https://pagopa.atlassian.net/browse/P4ADEV-2306
        }

        if (payer != null) {
            builder.payerEntityType(payer.getEntityType())
                    .payerUniqueIdentifierCode(Objects.requireNonNullElse(payer.getFiscalCode(), ANONYMOUS))
                    .payerFullName(payer.getFullName())
                    .payerAddress(payer.getAddress())
                    .payerStreetNumber(payer.getCivic())
                    .payerPostalCode(payer.getPostalCode())
                    .payerCity(payer.getLocation())
                    .payerProvince(payer.getProvince())
                    .payerCountry(payer.getNation())
                    .payerEmail(payer.getEmail());
        }

        return builder.build();
    }
}
