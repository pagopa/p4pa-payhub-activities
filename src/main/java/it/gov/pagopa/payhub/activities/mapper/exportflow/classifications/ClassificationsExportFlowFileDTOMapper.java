package it.gov.pagopa.payhub.activities.mapper.exportflow.classifications;

import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.Person;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class ClassificationsExportFlowFileDTOMapper {

    public ClassificationsExportFlowFileDTO map(ClassificationViewDTO retrievedObject) {

        Person payer = retrievedObject.getRecPayer();
        Person debtor = retrievedObject.getRecDebtor();

        ClassificationsExportFlowFileDTO.ClassificationsExportFlowFileDTOBuilder classificationsExportFlowFileDTOBuilder = ClassificationsExportFlowFileDTO.builder()
                .recFileName(retrievedObject.getRecFileName())
                .flowRowNumber(1)
                .recIud(retrievedObject.getRecIud())
                .recIuv(retrievedObject.getRecIuv())
                .recOrgFiscalCode(retrievedObject.getRecOrgFiscalCode())
                .recPaymentReceiptId(retrievedObject.getRecPaymentReceiptId())
                .recPaymentDateTime(retrievedObject.getRecPaymentDateTime())
                .requestMessageReferenceId(retrievedObject.getRecPaymentReceiptId())
                .requestReferenceDate(retrievedObject.getRecPaymentDateTime())
                .institutionAttTypeUniqueId("B")
                .recPspId(retrievedObject.getRecIdPsp())
                .recPspCompanyName(retrievedObject.getRecPspCompanyName())
                .beneficiaryUniqueIdType("G")
                .beneficiaryUniqueIdCode(retrievedObject.getRecOrgFiscalCode())
                .recBeneficiaryName(retrievedObject.getRecBeneficiaryOrgName());

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
                    .payerNation(payer.getNation())
                    .payerEmail(payer.getEmail());
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
                    .debtorNation(debtor.getNation())
                    .debtorEmail(debtor.getEmail());
        }

        classificationsExportFlowFileDTOBuilder
                .paymentOutcomeCode("0")
                .recPaymentAmount(Utilities.longCentsToBigDecimalEuro(retrievedObject.getRecPaymentAmount()))
                .uniquePaymentId(retrievedObject.getRecCreditorReferenceId())
                .paymentContextCode(retrievedObject.getRecPaymentReceiptId())
                .recTransferAmount(Utilities.longCentsToBigDecimalEuro(retrievedObject.getRecTransferAmount()))
                .singlePaymentOutcomeE("0")
                .singlePaymentOutcomeDateE(retrievedObject.getRecPaymentDateTime())
                .uniqueCollectionIdE(retrievedObject.getRecPaymentReceiptId())
                .recTransferRemittanceInformation(retrievedObject.getRecTransferRemittanceInformation())
                .recTransferCategory("9/" + retrievedObject.getRecTransferCategory())
                .recCreationDate(retrievedObject.getRecCreationDate())
                .recInstallmentBalance(retrievedObject.getRecInstallmentBalance())
                .payRepIuf(retrievedObject.getPayRepIuf())
                .payRepFlowDateTime(retrievedObject.getPayRepFlowDateTime())
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
                .payRepIuv(retrievedObject.getPayRepIuv())
                .payRepIur(retrievedObject.getPayRepIur())
                .singlePaymentAmountR(Utilities.longCentsToBigDecimalEuro(retrievedObject.getPayRepAmountPaidCents()))
                .singlePaymentOutcomeCodeR(retrievedObject.getPayRepPaymentOutcomeCode())
                .singlePaymentOutcomeDateR(retrievedObject.getPayRepPayDate())
                .acquisitionDateR(retrievedObject.getRecCreationDate())
                .tresAbiCode(retrievedObject.getTresAbiCode())
                .tresCabCode(retrievedObject.getTresCabCode())
                .tresAccountRegistryCode(retrievedObject.getTresAccountRegistryCode())
                .tresBillDate(retrievedObject.getTresBillDate())
                .tresRegionValueDate(retrievedObject.getTresRegionValueDate())
                .tresBillAmount(Utilities.longCentsToBigDecimalEuro(retrievedObject.getTresBillAmountCents()))
                .tresRemittanceCode(retrievedObject.getTresRemittanceCode())
                .tresLastName(retrievedObject.getTresLastName())
                .tresOrCode(retrievedObject.getTresLastName())
                .tresIuf(retrievedObject.getTresIuf())
                .tresIuv(retrievedObject.getTresIuv())
                .tresAcquisitionDateT(retrievedObject.getTresCreationDate())
                .tresBillYear(retrievedObject.getTresBillYear())
                .tresBillCode(retrievedObject.getTresBillCode())
                .domainUniqueId(retrievedObject.getTresDomainIdCode())
                .tresReceiptDate(retrievedObject.getTresReceptionDate())
                .tresDocumentYear(retrievedObject.getTresDocumentYear())
                .tresDocumentCode(retrievedObject.getTresDocumentCode())
                .tresProvisionalAe(retrievedObject.getTresProvisionalAe())
                .tresProvisionalCode(retrievedObject.getTresProvisionalCode())
                .tresActualSuspensionDate(retrievedObject.getTresActualSuspensionDate())
                .tresManagementProvisionalCode(retrievedObject.getTresManagementProvisionalCode())
                .lastClassificationDate(retrievedObject.getLastClassificationDate());

        return classificationsExportFlowFileDTOBuilder.build();
    }

}
