package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for defining an activity to delete classifications based on IUF.
 */
@ActivityInterface
public interface ClearClassifyIufActivity {
    /**
     * deletion of a classification based on the provided parameters
     *
     * @param organizationId organization id
     * @param iuf flow identifier
     * @return boolean true if success deletion or exception
     */
    @ActivityMethod
    boolean deleteClassificationByIuf(Long organizationId, String iuf);
}
