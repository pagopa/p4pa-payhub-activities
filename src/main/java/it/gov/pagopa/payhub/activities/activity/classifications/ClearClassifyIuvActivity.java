package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for defining an activity to delete classifications based on IUV.
 */
@ActivityInterface
public interface ClearClassifyIuvActivity {
	/**
	 * deletion of a classification based on the provided parameters
	 *
	 * @param organizationId organization id
	 * @param iuv creditor reference identifier
	 * @return boolean true if success deletion or false if fails
	 */
	@ActivityMethod
	boolean deleteClassificationByIuv(Long organizationId, String iuv);
}
