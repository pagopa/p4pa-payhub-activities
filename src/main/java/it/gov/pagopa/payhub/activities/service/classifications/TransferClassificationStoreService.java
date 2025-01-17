package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Lazy
@Slf4j
@Service
public class TransferClassificationStoreService {
	private final ClassificationDao classificationDao;

	public TransferClassificationStoreService(ClassificationDao classificationDao) {
		this.classificationDao = classificationDao;
	}

	/**
	 * Saves the provided classifications to the database.
	 * <p>
	 * It builds a list of
	 * {@link ClassificationDTO} objects based on the input data and saves them using the
	 * {@code classificationDao}.
	 *
	 * @param transferSemanticKeyDTO the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
	 * @param transferDTO            the DTO containing transfer details, may be {@code null}.
	 * @param paymentsReportingDTO   the DTO containing payment reporting details, may be {@code null}.
	 * @param treasuryDTO            the DTO containing treasury details, may be {@code null}.
	 * @param classifications        the list of classifications to be saved, represented as {@link ClassificationsEnum}.
	 * @return the list of classification saved in the database.
	 */
	public List<ClassificationDTO> saveClassifications(
		TransferSemanticKeyDTO transferSemanticKeyDTO,
		TransferDTO transferDTO,
		PaymentsReporting paymentsReportingDTO,
		Treasury treasuryDTO,
		List<ClassificationsEnum> classifications) {

		log.info("Saving classifications {} for semantic key organization id: {} and iuv: {} and iur {} and transfer index: {}",
			String.join(", ", classifications.stream().map(String::valueOf).toList()),
			transferSemanticKeyDTO.getOrgId(), transferSemanticKeyDTO.getIuv(), transferSemanticKeyDTO.getIur(), transferSemanticKeyDTO.getTransferIndex());

		Optional<PaymentsReporting> optionalPaymentsReporting = Optional.ofNullable(paymentsReportingDTO);
		List<ClassificationDTO> dtoList = classifications.stream()
			.map(classification -> ClassificationDTO.builder()
				.organizationId(transferSemanticKeyDTO.getOrgId())
				.transferId(Optional.ofNullable(transferDTO).map(TransferDTO::getTransferId).orElse(null))
				.paymentReportingId(optionalPaymentsReporting.map(PaymentsReporting::getPaymentsReportingId).orElse(null))
				.treasuryId(Optional.ofNullable(treasuryDTO).map(Treasury::getTreasuryId).orElse(null))
				.iuf(optionalPaymentsReporting.map(PaymentsReporting::getIuf).orElse(null))
				.iuv(transferSemanticKeyDTO.getIuv())
				.iur(transferSemanticKeyDTO.getIur())
				.transferIndex(transferSemanticKeyDTO.getTransferIndex())
				.classificationsEnum(classification)
				.build())
			.toList();

		return classificationDao.saveAll(dtoList);
	}
}
