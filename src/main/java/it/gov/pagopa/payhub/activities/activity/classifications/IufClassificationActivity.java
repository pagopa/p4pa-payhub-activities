package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;

/**
 * Interface for defining an activity to process payment reporting classifications based on IUF.
 */
@ActivityInterface
public interface IufClassificationActivity {

    /**
     * Processes IUF classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param treasuryId     the unique identifier of treasury
     * @param iuf            the unique identifier of the payment reporting flow (IUF)
     * @return IufClassificationActivityResult containing a list of payments and success flag
     */
    @ActivityMethod
    IufClassificationActivityResult classifyIuf(Long organizationId, String treasuryId, String iuf);
}