package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Slf4j
@Component
public class TransferClassificationActivityImpl implements TransferClassificationActivity {
	private final ClassificationDao classificationDao;
	private final TransferDao transferDao;
	private final PaymentsReportingDao paymentsReportingDao;
	private final TreasuryDao treasuryDao;
	private final TransferClassificationService transferClassificationService;
	private final TransferClassificationStoreService transferClassificationStoreService;

	public TransferClassificationActivityImpl(ClassificationDao classificationDao,
	                                          TransferDao transferDao,
	                                          PaymentsReportingDao paymentsReportingDao,
	                                          TreasuryDao treasuryDao,
	                                          TransferClassificationService transferClassificationService,
	                                          TransferClassificationStoreService transferClassificationStoreService) {
		this.classificationDao = classificationDao;
		this.transferDao = transferDao;
		this.paymentsReportingDao = paymentsReportingDao;
		this.treasuryDao = treasuryDao;
		this.transferClassificationService = transferClassificationService;
		this.transferClassificationStoreService = transferClassificationStoreService;
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

		transferClassificationStoreService.saveClassifications(transferSemanticKeyDTO, transferDTO, paymentsReportingDTO, treasuryDTO, classifications);
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
}
