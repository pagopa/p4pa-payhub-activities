package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class ClearClassifyIuvActivityImpl implements ClearClassifyIuvActivity {
    private final ClassificationDao classificationDao;

    public ClearClassifyIuvActivityImpl(ClassificationDao classificationDao) {
        this.classificationDao = classificationDao;
    }

    public boolean deleteClassificationByIuv(Long organizationId, String iuv) {
        log.debug("Deleting classifications for organization id: {} and iuv: {}", organizationId, iuv);
        return classificationDao.deleteClassificationByIuv(organizationId, iuv);
    }
}
