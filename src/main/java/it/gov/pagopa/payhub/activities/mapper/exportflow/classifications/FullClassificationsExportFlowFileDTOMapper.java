package it.gov.pagopa.payhub.activities.mapper.exportflow.classifications;

import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.FullClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.Person;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class FullClassificationsExportFlowFileDTOMapper {

    public ClassificationsExportFlowFileDTO map(FullClassificationViewDTO retrievedObject) {

        Person payer = retrievedObject.getRecPayer();
        Person debtor = retrievedObject.getRecDebtor();

        ClassificationsExportFlowFileDTO.ClassificationsExportFlowFileDTOBuilder classificationsExportFlowFileDTOBuilder = ClassificationsExportFlowFileDTO.builder()
                .flowName(retrievedObject.getRecFileName())
                .flowRowNumber(1)
                .iudE(retrievedObject.getRecIud())
                .iuvE(retrievedObject.getRecIuv())
                .orgFiscalCode(retrievedObject.getRecOrgFiscalCode())
                .paymentReceiptId(retrievedObject.getRecPaymentReceiptId())
                .paymentDateTime(retrievedObject.getRecPaymentDateTime())
                .requestMessageReferenceId(retrievedObject.getRecPaymentReceiptId())
                .requestReferenceDate(retrievedObject.getRecPaymentDateTime())
                .institutionAttTypeUniqueId("B")
                .pspId(retrievedObject.getRecIdPsp())
                .pspCompanyName(retrievedObject.getRecPspCompanyName())
                .beneficiaryUniqueIdType("G")
                .beneficiaryUniqueIdCode(retrievedObject.getRecOrgFiscalCode())
                .beneficiaryName(retrievedObject.getRecBeneficiaryOrgName());

        if (payer != null){
            classificationsExportFlowFileDTOBuilder
                    .payerUniqueIdType(payer.getEntityType())
                    .payerUniqueIdCode(payer.getEntityType())
                    .payerFullName(payer.getFullName())
                    .payerAddress(payer.getAddress())
                    .payerCivicNumber(payer.getCivic())
                    .payerPostalCode(payer.getPostalCode())
                    .payerLocation(payer.getLocation())
                    .payerProvince(payer.getProvince())
                    .payerCountry(payer.getNation())
                    .payerEmail(payer.getEmail())
                    .payerUniqueIdTypeI(payer.getEntityType())
                    .payerUniqueIdCodeI(payer.getEntityType())
                    .payerFullNameI(payer.getFullName())
                    .payerAddressI(payer.getAddress())
                    .payerCivicNumberI(payer.getCivic())
                    .payerPostalCodeI(payer.getPostalCode())
                    .payerLocationI(payer.getLocation())
                    .payerProvinceI(payer.getProvince())
                    .payerCountryI(payer.getNation())
                    .payerEmailI(payer.getEmail());
        }

        if (debtor != null){
            classificationsExportFlowFileDTOBuilder
                    .debtorUniqueIdType(debtor.getEntityType())
                    .debtorUniqueIdCode(debtor.getEntityType())
                    .debtorFullName(debtor.getFullName())
                    .debtorAddress(debtor.getAddress())
                    .debtorCivicNumber(debtor.getCivic())
                    .debtorPostalCode(debtor.getPostalCode())
                    .debtorLocation(debtor.getLocation())
                    .debtorProvince(debtor.getProvince())
                    .debtorCountry(debtor.getNation())
                    .debtorEmail(debtor.getEmail());
        }

        classificationsExportFlowFileDTOBuilder
                .paymentOutcomeCode("0")
                .totalPaidAmount(Utilities.longCentsToBigDecimalEuro(retrievedObject.getRecPaymentAmount()))
                .uniquePaymentId(retrievedObject.getRecCreditorReferenceId())
                .paymentContextCode(retrievedObject.getRecPaymentReceiptId())
                .singlePaymentAmountE(Utilities.longCentsToBigDecimalEuro(retrievedObject.getRecTransferAmount()))
                .singlePaymentOutcomeE("0")
                .singlePaymentOutcomeDateE(retrievedObject.getRecPaymentDateTime())
                .uniqueCollectionIdE(retrievedObject.getRecPaymentReceiptId())
                .remittanceInformation(retrievedObject.getRecTransferRemittanceInformation())
                .category("9/" + retrievedObject.getRecTransferCategory())
                .acquisitionDateE(retrievedObject.getRecCreationDate())
                .budget(retrievedObject.getRecInstallmentBalance())
                .flowDateTimeR(retrievedObject.getPayRepFlowDateTime())
                .uniqueRegulationCodeR(retrievedObject.getPayRepRegulationUniqueIdentifier())
                .regulationDateR(retrievedObject.getPayRepRegulationDate())
                .senderInstitutionUniqueIdType(retrievedObject.getPayRepSenderPspType())
                .senderInstitutionUniqueId(retrievedObject.getPayRepSenderPspCode())
                .senderInstitutionName(retrievedObject.getPayRepSenderPspName())
                .receiverInstitutionUniqueIdType(retrievedObject.getPayRepReceiverOrganizationType())
                .receiverInstitutionUniqueId(retrievedObject.getPayRepReceiverOrganizationCode())
                .receiverInstitutionName(retrievedObject.getPayRepReceiverOrganizationName())
                .totalPaymentsNumberR(retrievedObject.getPayRepTotalPayments())
                .totalPaymentsAmountR(Utilities.longCentsToBigDecimalEuro(retrievedObject.getPayRepTotalAmountCents()))
                .uniquePaymentIdentificationR(retrievedObject.getPayRepIuv())
                .uniqueCollectionIdentificationR(retrievedObject.getPayRepIur())
                .singlePaymentAmountR(Utilities.longCentsToBigDecimalEuro(retrievedObject.getPayRepAmountPaidCents()))
                .singlePaymentOutcomeCodeR(retrievedObject.getPayRepPaymentOutcomeCode())
                .singlePaymentOutcomeDateR(retrievedObject.getPayRepPayDate())
                .acquisitionDateR(retrievedObject.getRecCreationDate())
                .bankCode(retrievedObject.getTresAbiCode())
                .branchCode(retrievedObject.getTresCabCode())
                .treasuryAccountCode(retrievedObject.getTresAccountRegistryCode())
                .accountingDate(retrievedObject.getTresBillDate())
                .valueDate(retrievedObject.getTresRegionValueDate())
                .treasuryAmount(Utilities.longCentsToBigDecimalEuro(retrievedObject.getTresBillAmountCents()))
                .remittanceCodeT(retrievedObject.getTresRemittanceCode())
                .orderingPartyDescription(retrievedObject.getTresLastName())
                .or1Code(retrievedObject.getTresLastName())
                .iufT(retrievedObject.getTresIuf())
                .iuvT(retrievedObject.getTresIuv())
                .treasuryAcquisitionDateT(retrievedObject.getTresCreationDate())
                .billYear(retrievedObject.getTresBillYear())
                .billCode(retrievedObject.getTresBillCode())
                .domainUniqueId(retrievedObject.getTresDomainIdCode())
                .receiptDate(retrievedObject.getTresReceptionDate())
                .documentYear(retrievedObject.getTresDocumentYear())
                .documentCode(retrievedObject.getTresDocumentCode())
                .provisionalYear(retrievedObject.getTresProvisionalAe())
                .provisionalCode(retrievedObject.getTresProvisionalCode())
                .effectiveSuspensionDate(retrievedObject.getTresActualSuspensionDate())
                .provisionalManagementCode(retrievedObject.getTresManagementProvisionalCode())
                .lastUpdateDate(retrievedObject.getLastClassificationDate())

                .iudI(retrievedObject.getPayNoticeIud())
                .iuvI(retrievedObject.getPayNoticeIuv())
                .paymentExecutionDateI(retrievedObject.getPayNoticePaymentExecutionDate())
                .paymentTypeI(retrievedObject.getPayNoticePaymentType())
                .singlePaymentAmountI(Utilities.longCentsToBigDecimalEuro(retrievedObject.getPayNoticeAmountPaidCents()))
                .commissionChargeI(Utilities.longCentsToBigDecimalEuro(retrievedObject.getPayNoticePaCommission()))
                .remittanceInformationI(retrievedObject.getPayNoticeRemittanceInformation())
                .collectionSpecificDetailsI(retrievedObject.getPayNoticeTransferCategory())
                .dueTypeCodeI(retrievedObject.getPayNoticeDebtPositionTypeOrgCode())
                .balanceI(retrievedObject.getPayNoticeBalance());

        return ClassificationsExportFlowFileDTO.builder().build();
    }
}
