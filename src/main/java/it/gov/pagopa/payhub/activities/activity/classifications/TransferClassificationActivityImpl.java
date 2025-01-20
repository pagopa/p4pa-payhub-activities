package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Lazy
@Slf4j
@Component
public class TransferClassificationActivityImpl implements TransferClassificationActivity {
	private final ClassificationDao classificationDao;
	private final TransferDao transferDao;
	private final PaymentsReportingService paymentsReportingService;
	private final TransferClassificationService transferClassificationService;
	private final TransferClassificationStoreService transferClassificationStoreService;
	private final TreasuryService treasuryService;

	public TransferClassificationActivityImpl(ClassificationDao classificationDao,
                                              TransferDao transferDao,
                                              PaymentsReportingService paymentsReportingService,
                                              TransferClassificationService transferClassificationService,
                                              TransferClassificationStoreService transferClassificationStoreService,
											  TreasuryService treasuryService) {
		this.classificationDao = classificationDao;
		this.transferDao = transferDao;
		this.paymentsReportingService = paymentsReportingService;
		this.transferClassificationService = transferClassificationService;
		this.transferClassificationStoreService = transferClassificationStoreService;
        this.treasuryService = treasuryService;
    }

	@Override
	public void classify(TransferSemanticKeyDTO transferSemanticKey) {
		log.info("Transfer classification for organization id: {} and iuv: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv());
		if (!classificationDao.deleteTransferClassification(transferSemanticKey)) {
			throw new ClassificationException("Error occurred while clean up current processing Requests due to failed deletion");
		}
		TransferDTO transferDTO = transferDao.findBySemanticKey(transferSemanticKey);

		log.info("Retrieve payment reporting for organization id: {} and iuv: {} and iur {} and transfer index: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv(), transferSemanticKey.getIur(), transferSemanticKey.getTransferIndex());
		PaymentsReporting paymentsReporting = paymentsReportingService.getBySemanticKey(transferSemanticKey);
		Treasury treasuryDTO = retrieveTreasury(transferSemanticKey.getOrgId(), paymentsReporting);

		List<ClassificationsEnum> classifications = transferClassificationService.defineLabels(transferDTO, paymentsReportingDTO, treasuryDTO);
		log.info("Labels defined for organization id: {} and iuv: {} and iur {} and transfer index: {} are: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv(), transferSemanticKey.getIur(), transferSemanticKey.getTransferIndex(),
			String.join(", ", classifications.stream().map(String::valueOf).toList()));

		transferClassificationStoreService.saveClassifications(transferSemanticKey, transferDTO, paymentsReporting, treasuryDTO, classifications);
		notifyReportedTransferId(transferDTO, paymentsReporting);
	}

	/**
	 * Retrieves the {@link Treasury} record for the given ID.
	 *
	 * @param orgId the ID of the organization
	 * @return the {@link Treasury} corresponding to the given ID
	 */
	private Treasury retrieveTreasury(Long orgId, PaymentsReporting paymentsReportingDTO) {
		if (paymentsReportingDTO != null) {
			String iuf = paymentsReportingDTO.getIuf();
			log.info("Retrieve treasury for organization id: {} and iuf {}", orgId, iuf);
			return treasuryService.getByOrganizationIdAndIuf(orgId, iuf)
					.orElseThrow(() -> new InvalidValueException("invalid Treasury"));
		}
		return null;
	}

	/**
	 * Notify the status of the given transfer as Reported.
	 *
	 * @param transferDTO the transfer data transfer object containing transfer details
	 * @param paymentsReportingDTO the payments reporting data transfer object containing payment reporting details
	 */
	private void notifyReportedTransferId(TransferDTO transferDTO, PaymentsReporting paymentsReportingDTO) {
		if(transferDTO != null && paymentsReportingDTO != null) {
			transferDao.notifyReportedTransferId(transferDTO.getTransferId());
		}
	}
}
