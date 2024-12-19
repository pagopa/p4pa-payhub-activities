package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class ClearClassifyIufActivityImpl implements ClearClassifyIufActivity {
    private final ClassificationDao classificationDao;

    public ClearClassifyIufActivityImpl(ClassificationDao classificationDao) {
        this.classificationDao = classificationDao;
    }

    public boolean deleteClassificationByIuf(Long organizationId, String iuf) {
        log.debug("Deleting classification TES_NO_MATCH for organization id: {} and iuf: {}", organizationId,iuf);
        return classificationDao.deleteClassificationByIuf(organizationId, iuf, ClassificationsEnum.TES_NO_MATCH);
    }
}
