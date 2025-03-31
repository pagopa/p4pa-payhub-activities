package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * Service interface responsible for handling fine debt position reduction expiration.
 * Updates related statuses.
 */
@ActivityInterface
public interface DebtPositionFineReductionOptionExpirationActivity {

    /**
     * Processes the fine debt position for reduction expiration.
     *
     * @param debtPositionId the ID of the debt position
     * @return updated debt position ({@link DebtPositionDTO})
     */
    @ActivityMethod
    DebtPositionDTO handleFineReductionExpiration(Long debtPositionId);
}

