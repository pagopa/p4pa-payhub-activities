package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.exception.RetryableActivityException;
import it.gov.pagopa.payhub.activities.utility.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Implementation for defining an activity to delete classifications based on IUF.
 */
@Slf4j
@Lazy
@Component
public class ClearClassifyIufActivityImpl implements ClearClassifyIufActivity {
    private final ClassifyDao classifyDao;

    public ClearClassifyIufActivityImpl(ClassifyDao classifyDao) {
        this.classifyDao = classifyDao;
    }

    /**
     * delete classification
     *
     * @param organizationId organization id
     * @param iuf flow identifier
     * @return boolean true if success deletion or exception
     */
    public boolean deleteClassificationByIuf(Long organizationId, String iuf) {
        return classifyDao.deleteClassificationByIuf(organizationId, iuf, Utilities.CLASSIFICATION.TES_NO_MATCH.getValue());
    }
}
