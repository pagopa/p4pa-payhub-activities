package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for defining an activity to delete classifications based on Treasury.
 */
@ActivityInterface
public interface ClearClassifyTreasuryActivity {
    /**
     * deletion of a classification based on the provided parameters
     *
     * @param organizationId organization id
     * @param treasuryId treasury identifier
     * @return boolean true if success deletion or exception
     */
    @ActivityMethod
    Integer deleteClassificationByTreasuryId(Long organizationId, String treasuryId);
}
