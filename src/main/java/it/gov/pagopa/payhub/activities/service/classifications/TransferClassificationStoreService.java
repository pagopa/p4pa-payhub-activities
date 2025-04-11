package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
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
	private final InstallmentService installmentService;

	public TransferClassificationStoreService(ClassificationService classificationService,
	                                          ReceiptService receiptService,
	                                          DebtPositionTypeOrgService debtPositionTypeOrgService,
	                                          OrganizationService organizationService, IngestionFlowFileService ingestionFlowFileService,
	                                          InstallmentService installmentService) {
		this.classificationService = classificationService;
		this.receiptService = receiptService;
		this.debtPositionTypeOrgService = debtPositionTypeOrgService;
		this.organizationService = organizationService;
		this.ingestionFlowFileService = ingestionFlowFileService;
		this.installmentService = installmentService;
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
		PaymentsReporting paymentsReportingDTO,
		Treasury treasuryDTO,
		List<ClassificationsEnum> classifications) {
		Optional<PaymentsReporting> optionalPaymentsReporting = Optional.ofNullable(paymentsReportingDTO);
		Optional<Treasury> optionalTreasury = Optional.ofNullable(treasuryDTO);
		Optional<Transfer> optionalTransfer = Optional.ofNullable(transferDTO);
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
		Optional<InstallmentNoPII> optionalInstallment = optionalTransfer
			.flatMap(transfer -> {
		        log.debug("retrieving Installment with installmentId {}", transfer.getInstallmentId());
				return installmentService.getInstallmentById(transfer.getInstallmentId());
		    });
		Optional<Organization> optionalOrganization = optionalTransfer
			.flatMap(transfer -> {
				log.debug("retrieving Organization with fiscalCode {}", transfer.getOrgFiscalCode());
				return organizationService.getOrganizationByFiscalCode(transfer.getOrgFiscalCode());
			});
		Optional<IngestionFlowFile> optionalIngestionFlowFile = optionalInstallment
			.flatMap(installmentNoPII -> {
				log.debug("retrieving IngestionFlowFile with installmentId {}", installmentNoPII.getIngestionFlowFileId());
				return ingestionFlowFileService.findById(installmentNoPII.getIngestionFlowFileId());
			});

		log.info("Saving classifications {} for semantic key organization id: {} and iuv: {} and iur {} and transfer index: {}",
			String.join(", ", classifications.stream().map(String::valueOf).toList()),
			transferSemanticKeyDTO.getOrgId(), transferSemanticKeyDTO.getIuv(), transferSemanticKeyDTO.getIur(), transferSemanticKeyDTO.getTransferIndex());

		List<Classification> dtoList = classifications.stream()
			.map(classification -> (Classification) Classification.builder()
				.organizationId(transferSemanticKeyDTO.getOrgId())
				.transferId(optionalTransfer.map(Transfer::getTransferId).orElse(null))
				.paymentsReportingId(optionalPaymentsReporting.map(PaymentsReporting::getPaymentsReportingId).orElse(null))
				.treasuryId(optionalTreasury.map(Treasury::getTreasuryId).orElse(null))
				.iuf(optionalPaymentsReporting.map(PaymentsReporting::getIuf).orElse(null))
				.iuv(transferSemanticKeyDTO.getIuv())
				.iur(transferSemanticKeyDTO.getIur())
				.transferIndex(transferSemanticKeyDTO.getTransferIndex())
				.label(classification)
				.lastClassificationDate(LocalDate.now())
				.payDate(optionalPaymentsReporting.map(PaymentsReporting::getPayDate).orElse(null))
				.paymentDateTime(optionalReceipt.map(ReceiptNoPII::getPaymentDateTime).orElse(null))
				.regulationDate(optionalPaymentsReporting.map(PaymentsReporting::getRegulationDate).orElse(null))
				.billDate(optionalTreasury.map(Treasury::getBillDate).orElse(null))
				.regionValueDate(optionalTreasury.map(Treasury::getRegionValueDate).orElse(null))
				.pspCompanyName(optionalReceipt.map(ReceiptNoPII::getPspCompanyName).orElse(null))
				.pspLastName(optionalTreasury.map(Treasury::getPspLastName).orElse(null))
				.regulationUniqueIdentifier(optionalPaymentsReporting.map(PaymentsReporting::getRegulationUniqueIdentifier).orElse(null))
				.accountRegistryCode(optionalTreasury.map(Treasury::getAccountRegistryCode).orElse(null))
				.billAmountCents(optionalTreasury.map(Treasury::getBillAmountCents).orElse(null))
				.remittanceInformation(optionalTransfer.map(Transfer::getRemittanceInformation).orElse(null))
				.debtPositionTypeOrgCode(optionalDebtPositionTypeOrg.map(DebtPositionTypeOrg::getCode).orElse(null))
				.installmentIngestionFlowFileName(optionalIngestionFlowFile.map(IngestionFlowFile::getFileName).orElse(null))
				.receiptOrgFiscalCode(optionalReceipt.map(ReceiptNoPII::getOrgFiscalCode).orElse(null))
				.receiptPaymentReceiptId(optionalReceipt.map(ReceiptNoPII::getPaymentReceiptId).orElse(null))
				.receiptPaymentDateTime(optionalReceipt.map(ReceiptNoPII::getPaymentDateTime).orElse(null))
				.receiptPaymentRequestId(optionalReceipt.map(ReceiptNoPII::getReceiptId).map(String::valueOf).orElse(null))
				.receiptIdPsp(optionalReceipt.map(ReceiptNoPII::getIdPsp).orElse(null))
				.receiptPspCompanyName(optionalReceipt.map(ReceiptNoPII::getPspCompanyName).orElse(null))
				.organizationEntityType(optionalOrganization.map(Organization::getOrgTypeCode).orElse(null))
				.organizationName(optionalOrganization.map(Organization::getOrgName).orElse(null))
				.receiptPersonalDataId(optionalReceipt.map(ReceiptNoPII::getPersonalDataId).orElse(null))
				.receiptPaymentOutcomeCode(optionalReceipt.map(ReceiptNoPII::getOutcome).orElse(null))
				.receiptPaymentAmount(optionalReceipt.map(ReceiptNoPII::getPaymentAmountCents).orElse(null))
				.receiptCreditorReferenceId(optionalReceipt.map(ReceiptNoPII::getCreditorReferenceId).orElse(null))
				.transferAmount(optionalTransfer.map(Transfer::getAmountCents).orElse(null))
				.transferCategory(optionalTransfer.map(Transfer::getCategory).orElse(null))
				.receiptCreationDate(optionalReceipt.map(ReceiptNoPII::getCreationDate).orElse(null))
				.installmentBalance(optionalInstallment.map(InstallmentNoPII::getBalance).orElse(null))
				.build())
			.toList();

		return classificationService.saveAll(dtoList);
	}
}
