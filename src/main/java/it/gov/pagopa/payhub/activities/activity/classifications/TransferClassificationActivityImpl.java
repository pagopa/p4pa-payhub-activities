package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.service.classifications.LabelClassifier;
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
	private final List<LabelClassifier> classifiers;

	public TransferClassificationActivityImpl(ClassificationDao classificationDao,
	                                          TransferDao transferDao,
	                                          PaymentsReportingDao paymentsReportingDao,
	                                          TreasuryDao treasuryDao,
	                                          List<LabelClassifier> classifiers) {
		this.classificationDao = classificationDao;
		this.transferDao = transferDao;
		this.paymentsReportingDao = paymentsReportingDao;
		this.treasuryDao = treasuryDao;
		this.classifiers = classifiers;
	}

	@Override
	public void classify(Long orgId, String iuv, String iur, int transferIndex) {
		log.info("Transfer classification for organization id: {} and iuv: {}", orgId, iuv);
		if (!classificationDao.deleteTransferClassification(orgId, iuv, iur, transferIndex)) {
			throw new ClassificationException("Error occurred while clean up current processing Requests due to failed deletion");
		}
		Optional<TransferDTO> transfer = transferDao.findBySemanticKey(orgId, iuv, iur, transferIndex);

		log.info("Retrieve payment reporting for organization id: {} and iuv: {} and iur {} and transfer index: {}", orgId, iuv, iur, transferIndex);
		Optional<PaymentsReportingDTO> paymentsReporting =  paymentsReportingDao.findBySemanticKey(orgId, iuv, iur, transferIndex);
		Optional<TreasuryDTO> treasury = retrieveTreasury(orgId, paymentsReporting);
		//defineLabels(transfer, paymentsReporting, treasury);
	}

	private Optional<TreasuryDTO> retrieveTreasury(Long orgId, Optional<PaymentsReportingDTO> paymentsReporting) {
		if (paymentsReporting.isPresent()) {
			String iuf = paymentsReporting.get().getIuf();
			log.info("Retrieve treasury for organization id: {} and iuf {}", orgId, iuf);
			return treasuryDao.getByOrganizationIdAndIuf(orgId, iuf);
		}
		return Optional.empty();
	}

	private List<ClassificationsEnum> defineLabels(
		Optional<TransferDTO> transfer,
	    Optional<PaymentsReportingDTO> paymentsReporting,
	    Optional<TreasuryDTO> treasury) {

		return classifiers.stream()
			.map(classifier -> classifier.define(transfer, paymentsReporting, treasury))
			.flatMap(Optional::stream)
			.toList();
	}
}
