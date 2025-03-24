package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;


/**
 * Service interface responsible for handling custom fine debt positions.
 * This activity checks and updates the notificationDate and the statuses
 */
@ActivityInterface
public interface DebtPositionSynchronizeFineActivity {

    /**
     * Handle the fine debt position.
     *
     * @param debtPositionDTO the debt position to process
     * @param massive true if executed in a massive import context
     * @param executionParams workflow execution parameters (discounts, expiration, IO messages)
     * @return the updated debt position
     */
    @ActivityMethod
    DebtPositionDTO handleFineDebtPosition(DebtPositionDTO debtPositionDTO, boolean massive, FineWfExecutionConfig executionParams);
}

