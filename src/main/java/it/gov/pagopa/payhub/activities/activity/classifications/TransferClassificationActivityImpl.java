package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class TransferClassificationActivityImpl implements TransferClassificationActivity {
	private final ClassificationDao classificationDao;
	private final TransferDao transferDao;

	public TransferClassificationActivityImpl(ClassificationDao classificationDao,
	                                          TransferDao transferDao) {
		this.classificationDao = classificationDao;
		this.transferDao = transferDao;
	}

	@Override
	public void classify(Long orgId, String iuv, String iur, int transferIndex) {
		log.info("Transfer classification for organization id: {} and iuv: {}", orgId, iuv);
		if (!classificationDao.deleteTransferClassification(orgId, iuv, iur, transferIndex)) {
			throw new ClassificationException("Error occurred while clean up current processing Requests due to failed deletion");
		}
		transferDao.retrieveTransferByLogicalKey(orgId, iuv, iur, transferIndex);
	}
}
