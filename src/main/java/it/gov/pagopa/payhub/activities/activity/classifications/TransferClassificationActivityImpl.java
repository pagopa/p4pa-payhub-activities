package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class TransferClassificationActivityImpl implements TransferClassificationActivity {
    private final ClassificationDao classificationDao;

    public TransferClassificationActivityImpl(ClassificationDao classificationDao) {
        this.classificationDao = classificationDao;
    }

    @Override
    public boolean classify(Long orgId, String iuv, String iur, int transferIndex) {

        return cleanUpCurrentProcessingRequests(orgId, iuv, iur, transferIndex);
    }

    private boolean cleanUpCurrentProcessingRequests(Long orgId, String iuv, String iur, int transferIndex) {
        log.debug("Deleting classifications for organization id: {} and iuv: {}", orgId, iuv);
        return classificationDao.deleteClassificationByTransferKeySet(orgId, iuv, iur, transferIndex);
    }
}
