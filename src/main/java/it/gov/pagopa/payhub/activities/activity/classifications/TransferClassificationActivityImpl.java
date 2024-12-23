package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
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
	private final PaymentsReportingDao paymentsReportingDao;

	public TransferClassificationActivityImpl(ClassificationDao classificationDao,
											  TransferDao transferDao,
											  PaymentsReportingDao paymentsReportingDao) {
		this.classificationDao = classificationDao;
		this.transferDao = transferDao;
		this.paymentsReportingDao = paymentsReportingDao;
	}

	@Override
	public void classify(Long orgId, String iuv, String iur, int transferIndex) {
		log.info("Transfer classification for organization id: {} and iuv: {}", orgId, iuv);
		if (!classificationDao.deleteTransferClassification(orgId, iuv, iur, transferIndex)) {
			throw new ClassificationException("Error occurred while clean up current processing Requests due to failed deletion");
		}
		transferDao.findBySemanticKey(orgId, iuv, iur, transferIndex);

		log.info("Retrieve payment reporting for organization id: {} and iuv: {} and iur {} and transfer index: {}", orgId, iuv, iur, transferIndex);
		PaymentsReportingDTO paymentsReportingDTO =  paymentsReportingDao.findBySemanticKey(orgId, iuv, iur, transferIndex);
		if (paymentsReportingDTO==null) {
			log.info("Payment reporting with specified parameters not found");
		}
	}
}
