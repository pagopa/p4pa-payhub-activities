package it.gov.pagopa.payhub.activities.activity.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;

import java.util.Map;

/**
 * Service class responsible for finalizing the update of the status of a debt position
 */
@ActivityInterface
public interface FinalizeDebtPositionSyncStatusActivity {

    /**
     * Finalizes the update of the debt position status from the installments
     *
     * @param debtPositionId the identifier of the debt position to be updated
     * @param syncStatusDTO the map of IUD and {@link IupdSyncStatusUpdateDTO} containing new status and IUPD PagoPa of installment
     */
    @ActivityMethod
    DebtPositionDTO finalizeDebtPositionSyncStatus(Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusDTO);

}
