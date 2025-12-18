package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.*;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Lazy
@Slf4j
@Service
public class TransferClassificationStoreService {
    private final ClassificationService classificationService;
    private final ReceiptService receiptService;
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final OrganizationService organizationService;
    private final IngestionFlowFileService ingestionFlowFileService;
    private final DebtPositionService debtPositionService;

    public TransferClassificationStoreService(ClassificationService classificationService,
                                              ReceiptService receiptService,
                                              DebtPositionTypeOrgService debtPositionTypeOrgService,
                                              OrganizationService organizationService,
                                              IngestionFlowFileService ingestionFlowFileService,
                                              DebtPositionService debtPositionService) {
        this.classificationService = classificationService;
        this.receiptService = receiptService;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.organizationService = organizationService;
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.debtPositionService = debtPositionService;
    }

    /**
     * Saves the provided classifications to the database.
     * <p>
     * It builds a list of
     * {@link Classification} objects based on the input data and saves them using the
     * {@code classificationDao}.
     *
     * @param transferSemanticKeyDTO the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
     * @param transferDTO            the DTO containing transfer details, may be {@code null}.
     * @param paymentsReportingDTO   the DTO containing payment reporting details, may be {@code null}.
     * @param treasuryDTO            the DTO containing treasury details, may be {@code null}.
     * @param classifications        the list of classifications to be saved, represented as {@link ClassificationsEnum}.
     * @return the number of classification saved
     */
    public Integer saveClassifications(
        TransferSemanticKeyDTO transferSemanticKeyDTO,
        Transfer transferDTO,
        InstallmentNoPII installmentDTO,
        PaymentsReporting paymentsReportingDTO,
        Treasury treasuryDTO,
        PaymentNotificationNoPII paymentNotificationDTO,
        List<ClassificationsEnum> classifications) {
        Optional<PaymentsReporting> optionalPaymentsReporting = Optional.ofNullable(paymentsReportingDTO);
        Optional<Treasury> optionalTreasury = Optional.ofNullable(treasuryDTO);
        Optional<Transfer> optionalTransfer = Optional.ofNullable(transferDTO);
        Optional<PaymentNotificationNoPII> optionalPaymentNotification = Optional.ofNullable(paymentNotificationDTO);
        Optional<InstallmentNoPII> optionalInstallment = Optional.ofNullable(installmentDTO);
        Optional<ReceiptNoPII> optionalReceipt = optionalTransfer
            .map(transfer -> {
                log.debug("retrieving Receipt from Transfer ID {}", transfer.getTransferId());
                return receiptService.getByTransferId(transfer.getTransferId());
            });
        Optional<DebtPositionTypeOrg> optionalDebtPositionTypeOrg = optionalTransfer
            .map(transfer -> {
                log.debug("retrieving DebtPositionTypeOrg with installmentId {}", transfer.getInstallmentId());
                return debtPositionTypeOrgService.getDebtPositionTypeOrgByInstallmentId(transfer.getInstallmentId());
            });
        Optional<Organization> optionalOrganization = optionalTransfer
            .flatMap(transfer -> {
                log.debug("retrieving Organization with fiscalCode {}", transfer.getOrgFiscalCode());
                return organizationService.getOrganizationByFiscalCode(transfer.getOrgFiscalCode());
            })
            .or(() ->
                organizationService.getOrganizationById(transferSemanticKeyDTO.getOrgId())
            );
        Optional<IngestionFlowFile> optionalIngestionFlowFile = optionalInstallment
            .map(InstallmentNoPII::getIngestionFlowFileId)
            .flatMap(ingestionFlowFileId -> {
                log.debug("retrieving IngestionFlowFile with ingestionFlowFileId {}", ingestionFlowFileId);
                return ingestionFlowFileService.findById(ingestionFlowFileId);
            });
        Optional<DebtPosition> optionalDebtPosition = optionalInstallment
            .map(InstallmentNoPII::getInstallmentId)
            .flatMap(installmentId -> {
                log.debug("retrieving DebtPosition with installmentId {}", installmentId);
                return debtPositionService.getDebtPositionByInstallmentId(installmentId);
            });

        log.info("Saving classifications {} for semantic key organization id: {} and iuv: {} and iur {} and transfer index: {}",
            String.join(", ", classifications.stream().map(String::valueOf).toList()),
            transferSemanticKeyDTO.getOrgId(), transferSemanticKeyDTO.getIuv(), transferSemanticKeyDTO.getIur(), transferSemanticKeyDTO.getTransferIndex());

        List<Classification> dtoList = classifications.stream()
            .map(classification -> (Classification) Classification.builder()
                // TransferSemanticKeyDTO fields
                .organizationId(transferSemanticKeyDTO.getOrgId())
                .iuv(transferSemanticKeyDTO.getIuv())
                .iur(transferSemanticKeyDTO.getIur())
                .transferIndex(transferSemanticKeyDTO.getTransferIndex())

                // Transfer fields
                .transferId(optionalTransfer.map(Transfer::getTransferId).orElse(null))
                .remittanceInformation(optionalTransfer.map(Transfer::getRemittanceInformation)
                    .orElse(optionalPaymentNotification.map(PaymentNotificationNoPII::getRemittanceInformation).orElse(null)))
                .transferAmount(optionalTransfer.map(Transfer::getAmountCents).orElse(null))
                .transferCategory(optionalTransfer.map(Transfer::getCategory).orElse(null))

                // PaymentsReporting fields
                .paymentsReportingId(optionalPaymentsReporting.map(PaymentsReporting::getPaymentsReportingId).orElse(null))
                .iuf(optionalPaymentsReporting.map(PaymentsReporting::getIuf)
                    .orElse(optionalTreasury.map(Treasury::getIuf).orElse(null)))
                .payDate(optionalPaymentsReporting.map(PaymentsReporting::getPayDate)
                    .orElse(optionalPaymentNotification.map(PaymentNotificationNoPII::getPaymentExecutionDate).orElse(null)))
                .regulationDate(optionalPaymentsReporting.map(PaymentsReporting::getRegulationDate).orElse(null))
                .regulationUniqueIdentifier(optionalPaymentsReporting.map(PaymentsReporting::getRegulationUniqueIdentifier).orElse(null))

                // Treasury fields
                .treasuryId(optionalTreasury.map(Treasury::getTreasuryId).orElse(null))
                .billDate(optionalTreasury.map(Treasury::getBillDate).orElse(null))
                .regionValueDate(optionalTreasury.map(Treasury::getRegionValueDate).orElse(null))
                .accountRegistryCode(optionalTreasury.map(Treasury::getAccountRegistryCode).orElse(null))
                .billAmountCents(optionalTreasury.map(Treasury::getBillAmountCents).orElse(null))
                .pspLastName(optionalTreasury.map(Treasury::getPspLastName).orElse(null))
                .billCode(optionalTreasury.map(Treasury::getBillCode).orElse(null))
                .billYear(optionalTreasury.map(Treasury::getBillYear).orElse(null))
                .documentCode(optionalTreasury.map(Treasury::getDocumentCode).orElse(null))
                .documentYear(optionalTreasury.map(Treasury::getDocumentYear).orElse(null))
                .provisionalAe(optionalTreasury.map(Treasury::getProvisionalAe).orElse(null))
                .provisionalCode(optionalTreasury.map(Treasury::getProvisionalCode).orElse(null))

                // Receipt fields
                .paymentDateTime(optionalReceipt.map(ReceiptNoPII::getPaymentDateTime).orElse(null))
                .pspCompanyName(optionalReceipt.map(ReceiptNoPII::getPspCompanyName).orElse(null))
                .receiptOrgFiscalCode(optionalReceipt.map(ReceiptNoPII::getOrgFiscalCode).orElse(null))
                .receiptPaymentReceiptId(optionalReceipt.map(ReceiptNoPII::getPaymentReceiptId).orElse(null))
                .receiptPaymentRequestId(optionalReceipt.map(ReceiptNoPII::getReceiptId).map(String::valueOf).orElse(null))
                .receiptIdPsp(optionalReceipt.map(ReceiptNoPII::getIdPsp).orElse(null))
                .receiptPspCompanyName(optionalReceipt.map(ReceiptNoPII::getPspCompanyName).orElse(null))
                .receiptPersonalDataId(optionalReceipt.map(ReceiptNoPII::getPersonalDataId).orElse(null))
                .receiptPaymentOutcomeCode(optionalReceipt.map(ReceiptNoPII::getOutcome).orElse(null))
                .receiptPaymentAmount(optionalReceipt.map(ReceiptNoPII::getPaymentAmountCents).orElse(null))
                .receiptCreditorReferenceId(optionalReceipt.map(ReceiptNoPII::getCreditorReferenceId).orElse(null))
                .receiptCreationDate(optionalReceipt.map(ReceiptNoPII::getCreationDate).orElse(null))
                .receiptPaymentDateTime(optionalReceipt.map(ReceiptNoPII::getPaymentDateTime).orElse(null))

                // Installment fields
                .iud(optionalInstallment.map(InstallmentNoPII::getIud)
                    .orElse(optionalPaymentNotification.map(PaymentNotificationNoPII::getIud).orElse(null)))
                .installmentBalance(optionalInstallment.map(InstallmentNoPII::getBalance)
                    .orElse(optionalPaymentNotification.map(PaymentNotificationNoPII::getBalance).orElse(null)))
                .debtorFiscalCodeHash(optionalInstallment.map(InstallmentNoPII::getDebtorFiscalCodeHash)
                    .orElse(optionalPaymentNotification.map(PaymentNotificationNoPII::getDebtorFiscalCodeHash).orElse(null)))

                // IngestionFlowFile fields
                .installmentIngestionFlowFileName(optionalIngestionFlowFile.map(IngestionFlowFile::getFileName).orElse(null))

                // DebtPositionTypeOrg fields
                .debtPositionTypeOrgCode(optionalDebtPositionTypeOrg.map(DebtPositionTypeOrg::getCode)
                    .orElse(optionalPaymentNotification.map(PaymentNotificationNoPII::getDebtPositionTypeOrgCode).orElse(null)))
                .debtPositionTypeOrgDescription(optionalDebtPositionTypeOrg.map(DebtPositionTypeOrg::getDescription).orElse(null))

                // Organization fields
                .organizationEntityType(optionalOrganization.map(Organization::getOrgTypeCode).orElse(null))
                .organizationName(optionalOrganization.map(Organization::getOrgName).orElse(null))

                // Payment Notification fields
                .paymentNotificationId(optionalPaymentNotification.map(PaymentNotificationNoPII::getPaymentNotificationId).orElse(null))

                // Classification-specific fields
                .label(classification)
                .lastClassificationDate(LocalDate.now())

                // Origins
                .debtPositionOrigin(optionalDebtPosition.map(DebtPosition::getDebtPositionOrigin).orElse(null))
                .receiptOrigin(optionalReceipt.map(ReceiptNoPII::getReceiptOrigin).orElse(null))
                .build())
            .toList();

        return classificationService.saveAll(dtoList);
    }

    public Integer saveIudClassifications(
        PaymentNotificationNoPII paymentNotificationNoPII,
        List<ClassificationsEnum> classifications) {
        return saveClassifications(
            new TransferSemanticKeyDTO(paymentNotificationNoPII.getOrganizationId(), paymentNotificationNoPII.getIuv(), null, null),
            null,
            null,
            null,
            null,
            paymentNotificationNoPII,
            classifications);
    }

    public Integer saveIufClassifications(
        Treasury treasury,
        List<ClassificationsEnum> classifications) {
        return saveClassifications(
            new TransferSemanticKeyDTO(treasury.getOrganizationId(), null, null, null),
            null,
            null,
            null,
            treasury,
            null,
            classifications);
    }
}
