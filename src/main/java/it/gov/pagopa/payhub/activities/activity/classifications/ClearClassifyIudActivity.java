package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for defining an activity to delete classifications based on IUD.
 */
@ActivityInterface
public interface ClearClassifyIudActivity {
	/**
	 * deletion of a classification based on the provided parameters
	 *
	 * @param organizationId organization id
	 * @param iud debt position unique identifier
	 * @return boolean true if success deletion or exception
	 */
	@ActivityMethod
	Long deleteClassificationByIud(Long organizationId, String iud);
}
