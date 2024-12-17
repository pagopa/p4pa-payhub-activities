package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
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
     * deletion of a classification based on the provided parameters
     *
     * @param organizationId organization id
     * @param iuf flow identifier
     * @throws Exception exception thrown
     */
    public void deleteClassificationByIuf(Long organizationId, String iuf) throws Exception {
        try {
            classifyDao.deleteClassificationByIuf(organizationId, iuf, Utilities.CLASSIFICATION.TES_NO_MATCH.getValue());
        }
        catch (NotRetryableActivityException notRetryableActivityException) {
            log.error("Activity not retryable for errors in deleting classification TES_NO_MATCH for organizationId id {} and iuf {}", organizationId, iuf);
            throw notRetryableActivityException;
        }
        catch (Exception exception) {
            log.error("Error deleting classification TES_NO_MATCH for organizationId id {} and iuf {}", organizationId, iuf);
            throw exception;
        }
    }
}
