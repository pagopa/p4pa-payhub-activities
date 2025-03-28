package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.PaidInstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.EntityIdentifierType;
import it.gov.pagopa.payhub.activities.enums.UniqueIdentifierType;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Person;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class InstallmentExportFlowFileDTOMapper {

    private static final String ANONYMOUS = "ANONIMO";
    private static final String MARCA_BOLLO = "MARCA_BOLLO";
    private static final String RECEIPT_ATTACHMENT_TYPE = "BD";

    public PaidInstallmentExportFlowFileDTO map(InstallmentPaidViewDTO installmentPaidViewDTO) {

        Person debtor = installmentPaidViewDTO.getDebtor();
        Person payer = installmentPaidViewDTO.getPayer();

        PaidInstallmentExportFlowFileDTO.PaidInstallmentExportFlowFileDTOBuilder builder = PaidInstallmentExportFlowFileDTO.builder()
                .iuf(installmentPaidViewDTO.getIuf())
                .flowRowNumber(1)
                .iud(installmentPaidViewDTO.getIud())
                .iuv(installmentPaidViewDTO.getNoticeNumber())
                .domainIdentifier(installmentPaidViewDTO.getOrgFiscalCode())
                .receiptMessageIdentifier(installmentPaidViewDTO.getPaymentReceiptId())
                .receiptMessageDateTime(installmentPaidViewDTO.getPaymentDateTime())
                .requestMessageReference(installmentPaidViewDTO.getPaymentReceiptId())
                .requestDateTimeReference(installmentPaidViewDTO.getPaymentDateTime())
                .uniqueIdentifierType(UniqueIdentifierType.B)
                .uniqueIdentifierCode(installmentPaidViewDTO.getIdPsp())
                .attestingName(installmentPaidViewDTO.getPspCompanyName())
                .beneficiaryEntityType(EntityIdentifierType.G)
                .beneficiaryUniqueIdentifierCode(installmentPaidViewDTO.getOrgFiscalCode())
                .beneficiaryName(installmentPaidViewDTO.getCompanyName())
                .debtorEntityType(debtor.getEntityType() != null ? EntityIdentifierType.valueOf(debtor.getEntityType().getValue()) : null)
                .debtorUniqueIdentifierCode(debtor.getFiscalCode() != null ? debtor.getFiscalCode() : ANONYMOUS)
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
                .singlePaymentOutcomeDateTime(installmentPaidViewDTO.getPaymentDateTime())
                .uniqueCollectionIdentifier(installmentPaidViewDTO.getPaymentReceiptId())
                .paymentReason(installmentPaidViewDTO.getRemittanceInformation())
                .collectionSpecificData("9/".concat(installmentPaidViewDTO.getCategory()))
                .dueType(installmentPaidViewDTO.getCode())
                .rt(null) // TODO: field rt depends on task https://pagopa.atlassian.net/browse/P4ADEV-2306
                .singlePaymentDataIndex(installmentPaidViewDTO.getTransferIndex())
                .pspAppliedFees(installmentPaidViewDTO.getFeeCents() != null ? Utilities.longCentsToBigDecimalEuro(installmentPaidViewDTO.getFeeCents()) : null)
                .balance(installmentPaidViewDTO.getBalance())
                .orgFiscalCode(installmentPaidViewDTO.getOrgFiscalCode())
                .orgName(installmentPaidViewDTO.getCompanyName())
                .dueTaxonomicCode(installmentPaidViewDTO.getCategory());

        if (MARCA_BOLLO.equals(installmentPaidViewDTO.getCode())) {
            builder.receiptAttachmentType(RECEIPT_ATTACHMENT_TYPE)
            .receiptAttachmentTest(null); // TODO: field blbRtDatiPagDatiSingPagAllegatoRicevutaTest depends on task https://pagopa.atlassian.net/browse/P4ADEV-2306
        }

        if (payer != null) {
            builder.payerEntityType(payer.getEntityType() != null ? EntityIdentifierType.valueOf(payer.getEntityType().getValue()) : null)
                    .payerUniqueIdentifierCode(payer.getFiscalCode() != null ? payer.getFiscalCode() : ANONYMOUS)
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
