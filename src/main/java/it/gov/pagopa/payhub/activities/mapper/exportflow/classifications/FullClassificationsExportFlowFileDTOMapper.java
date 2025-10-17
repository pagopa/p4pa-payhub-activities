package it.gov.pagopa.payhub.activities.mapper.exportflow.classifications;

import it.gov.pagopa.payhub.activities.dto.exportflow.classifications.ClassificationsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.FullClassificationViewDTO;
import it.gov.pagopa.pu.classification.dto.generated.PersonDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Lazy
@Component
public class FullClassificationsExportFlowFileDTOMapper {

    public ClassificationsExportFlowFileDTO map(FullClassificationViewDTO retrievedObject) {

        PersonDTO payer = retrievedObject.getRecPayer();
        PersonDTO debtor = retrievedObject.getRecDebtor();

        LocalDate recPaymentDate = retrievedObject.getRecPaymentDateTime() != null
                ? retrievedObject.getRecPaymentDateTime().toLocalDate()
                : null;
        LocalDate recCreationDate = retrievedObject.getRecCreationDate() != null
                ? retrievedObject.getRecCreationDate().toLocalDate()
                : null;

        ClassificationsExportFlowFileDTO.ClassificationsExportFlowFileDTOBuilder classificationsExportFlowFileDTOBuilder = ClassificationsExportFlowFileDTO.builder()
                .recFileName(retrievedObject.getRecFileName())
                .flowRowNumber(1)
                .recIud(retrievedObject.getRecIud())
                .recIuv(retrievedObject.getRecIuv())
                .recOrgFiscalCode(retrievedObject.getRecOrgFiscalCode())
                .recPaymentReceiptId(retrievedObject.getRecPaymentReceiptId())
                .recPaymentDateTime(retrievedObject.getRecPaymentDateTime() != null
                        ? retrievedObject.getRecPaymentDateTime().toLocalDateTime()
                        : null)
                .requestMessageReferenceId(retrievedObject.getRecPaymentReceiptId())
                .requestReferenceDate(recPaymentDate)
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
                .singlePaymentOutcomeDateE(recPaymentDate)
                .uniqueCollectionIdE(retrievedObject.getRecPaymentReceiptId())
                .recTransferRemittanceInformation(retrievedObject.getRecTransferRemittanceInformation())
                .recTransferCategory("9/" + retrievedObject.getRecTransferCategory())
                .recCreationDate(recCreationDate)
                .recInstallmentBalance(retrievedObject.getRecInstallmentBalance())
                .payRepFlowDateTime(retrievedObject.getPayRepFlowDateTime() != null
                        ? retrievedObject.getPayRepFlowDateTime().toLocalDateTime()
                        : null)
                .payRepIuf(retrievedObject.getPayRepIuf())
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
                .acquisitionDateR(recCreationDate)
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
                .tresAcquisitionDateT(retrievedObject.getTresCreationDate() != null
                        ? retrievedObject.getTresCreationDate().toLocalDate()
                        : null)
                .tresBillYear(retrievedObject.getTresBillYear())
                .tresBillCode(retrievedObject.getTresBillCode())
                .domainUniqueId(retrievedObject.getTresDomainIdCode())
                .tresReceiptDate(retrievedObject.getTresReceptionDate() != null
                        ? retrievedObject.getTresReceptionDate().toLocalDate()
                        : null)
                .tresDocumentYear(retrievedObject.getTresDocumentYear())
                .tresDocumentCode(retrievedObject.getTresDocumentCode())
                .tresProvisionalAe(retrievedObject.getTresProvisionalAe())
                .tresProvisionalCode(retrievedObject.getTresProvisionalCode())
                .tresActualSuspensionDate(retrievedObject.getTresActualSuspensionDate())
                .tresManagementProvisionalCode(retrievedObject.getTresManagementProvisionalCode())
                .lastClassificationDate(retrievedObject.getLastClassificationDate())

                .payNoticeIud(retrievedObject.getPayNoticeIud())
                .payNoticeIuv(retrievedObject.getPayNoticeIuv())
                .payNoticePaymentExecutionDate(retrievedObject.getPayNoticePaymentExecutionDate())
                .payNoticePaymentType(retrievedObject.getPayNoticePaymentType())
                .singlePaymentAmountI(Utilities.longCentsToBigDecimalEuro(retrievedObject.getPayNoticeAmountPaidCents()))
                .payNoticePaCommission(Utilities.longCentsToBigDecimalEuro(retrievedObject.getPayNoticePaCommissionCents()))
                .payNoticeRemittanceInformation(retrievedObject.getPayNoticeRemittanceInformation())
                .payNoticeTransferCategory(retrievedObject.getPayNoticeTransferCategory())
                .payNoticeDebtPositionTypeOrgCode(retrievedObject.getPayNoticeDebtPositionTypeOrgCode())
                .payNoticeBalance(retrievedObject.getPayNoticeBalance());

        return classificationsExportFlowFileDTOBuilder.build();
    }
}
