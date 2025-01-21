package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;

import java.util.Map;

/**
 * Service class responsible for finalizing the update of the status of a debt position
 */
public interface FinalizeDebtPositionSyncStatusActivity {

    /**
     * Finalizes the update of the debt position status from the installments
     *
     * @param debtPositionId the identifier of the debt position to be updated
     * @param syncStatusDTO the map of IUD and {@link IupdSyncStatusUpdateDTO} containing new status and IUPD PagoPa of installment
     */
    DebtPositionDTO finalizeDebtPositionSyncStatus(Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusDTO);

}
