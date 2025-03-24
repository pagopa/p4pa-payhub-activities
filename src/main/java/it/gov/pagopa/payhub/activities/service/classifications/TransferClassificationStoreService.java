package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
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

	public TransferClassificationStoreService(ClassificationService classificationService,
	                                          ReceiptService receiptService,
	                                          DebtPositionTypeOrgService debtPositionTypeOrgService) {
		this.classificationService = classificationService;
		this.receiptService = receiptService;
		this.debtPositionTypeOrgService = debtPositionTypeOrgService;
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
				.label(classification.name())
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
				.build())
			.toList();

		return classificationService.saveAll(dtoList);
	}
}
