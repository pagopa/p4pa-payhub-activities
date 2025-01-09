package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Lazy
@Slf4j
@Component
public class TransferClassificationActivityImpl implements TransferClassificationActivity {
	private final ClassificationDao classificationDao;
	private final TransferDao transferDao;
	private final PaymentsReportingDao paymentsReportingDao;
	private final TreasuryDao treasuryDao;
	private final TransferClassificationService transferClassificationService;

	public TransferClassificationActivityImpl(ClassificationDao classificationDao,
	                                          TransferDao transferDao,
	                                          PaymentsReportingDao paymentsReportingDao,
	                                          TreasuryDao treasuryDao,
	                                          TransferClassificationService transferClassificationService) {
		this.classificationDao = classificationDao;
		this.transferDao = transferDao;
		this.paymentsReportingDao = paymentsReportingDao;
		this.treasuryDao = treasuryDao;
		this.transferClassificationService = transferClassificationService;
	}

	@Override
	public void classify(Long orgId, String iuv, String iur, int transferIndex) {
		log.info("Transfer classification for organization id: {} and iuv: {}", orgId, iuv);
		if (!classificationDao.deleteTransferClassification(orgId, iuv, iur, transferIndex)) {
			throw new ClassificationException("Error occurred while clean up current processing Requests due to failed deletion");
		}
		TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
			.orgId(orgId)
			.iuv(iuv)
			.iur(iur)
			.transferIndex(transferIndex)
			.build();

		TransferDTO transferDTO = transferDao.findBySemanticKey(transferSemanticKeyDTO);

		log.info("Retrieve payment reporting for organization id: {} and iuv: {} and iur {} and transfer index: {}", orgId, iuv, iur, transferIndex);
		PaymentsReportingDTO paymentsReportingDTO =  paymentsReportingDao.findBySemanticKey(orgId, iuv, iur, transferIndex);
		TreasuryDTO treasuryDTO = retrieveTreasury(orgId, paymentsReportingDTO);

		List<ClassificationsEnum> classifications = transferClassificationService.defineLabels(transferDTO, paymentsReportingDTO, treasuryDTO);
		log.info("Labels defined for organization id: {} and iuv: {} and iur {} and transfer index: {} are: {}",
			orgId, iuv, iur, transferIndex, String.join(", ", classifications.stream().map(String::valueOf).toList()));

		saveClassifications(transferSemanticKeyDTO, transferDTO, paymentsReportingDTO, treasuryDTO, classifications);
	}

	/**
	 * Retrieves the {@link TreasuryDTO} record for the given ID.
	 *
	 * @param orgId the ID of the organization
	 * @return the {@link TreasuryDTO} corresponding to the given ID
	 */
	private TreasuryDTO retrieveTreasury(Long orgId, PaymentsReportingDTO paymentsReportingDTO) {
		if (paymentsReportingDTO != null) {
			String iuf = paymentsReportingDTO.getIuf();
			log.info("Retrieve treasury for organization id: {} and iuf {}", orgId, iuf);
			return treasuryDao.getByOrganizationIdAndIuf(orgId, iuf);
		}
		return null;
	}

	/**
	 * Saves the provided classifications to the database.
	 *
	 * It builds a list of
	 * {@link ClassificationDTO} objects based on the input data and saves them using the
	 * {@code classificationDao}.
	 *
	 * @param transferSemanticKeyDTO the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
	 * @param transferDTO the DTO containing transfer details, may be {@code null}.
	 * @param paymentsReportingDTO the DTO containing payment reporting details, may be {@code null}.
	 * @param treasuryDTO the DTO containing treasury details, may be {@code null}.
	 * @param classifications the list of classifications to be saved, represented as {@link ClassificationsEnum}.
	 */
	private void saveClassifications(
		TransferSemanticKeyDTO transferSemanticKeyDTO,
		TransferDTO transferDTO,
		PaymentsReportingDTO paymentsReportingDTO,
		TreasuryDTO treasuryDTO,
		List<ClassificationsEnum> classifications) {

		log.info("Saving classifications {} for semantic key organization id: {} and iuv: {} and iur {} and transfer index: {}",
			String.join(", ", classifications.stream().map(String::valueOf).toList()),
			transferSemanticKeyDTO.getOrgId(), transferSemanticKeyDTO.getIuv(), transferSemanticKeyDTO.getIur(), transferSemanticKeyDTO.getTransferIndex());

		Optional<PaymentsReportingDTO> optionalPaymentsReportingDTO = Optional.ofNullable(paymentsReportingDTO);
		List<ClassificationDTO> dtoList = classifications.stream()
			.map(classification -> ClassificationDTO.builder()
				.organizationId(transferSemanticKeyDTO.getOrgId())
				.transferId(Optional.ofNullable(transferDTO).map(TransferDTO::getTransferId).orElse(null))
				.paymentReportingId(optionalPaymentsReportingDTO.map(PaymentsReportingDTO::getPaymentsReportingId).orElse(null))
				.treasuryId(Optional.ofNullable(treasuryDTO).map(TreasuryDTO::getTreasuryId).orElse(null))
				.iuf(optionalPaymentsReportingDTO.map(PaymentsReportingDTO::getIuf).orElse(null))
				.iuv(transferSemanticKeyDTO.getIuv())
				.iur(transferSemanticKeyDTO.getIur())
				.transferIndex(transferSemanticKeyDTO.getTransferIndex())
				.classificationsEnum(classification)
				.build())
			.toList();

		classificationDao.saveAll(dtoList);
	}

}
