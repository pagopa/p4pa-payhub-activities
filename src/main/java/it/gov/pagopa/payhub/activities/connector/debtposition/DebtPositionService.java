package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * This interface provides methods that manage debt positions within the related microservice
 */
public interface DebtPositionService {

    /**
     * Finalizes the update of the debt position status from the installments
     *
     * @param debtPositionId the identifier of the debt position to be updated
     * @param syncStatusUpdateDTO the map of IUD and {@link IupdSyncStatusUpdateDTO} containing new status and IUPD PagoPa of installment
     */
    DebtPositionDTO finalizeSyncStatus(Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusUpdateDTO);

    /**
     * Checks and updates the expiration date of the installments associated to a debt position
     *
     * @param debtPositionId the unique identifier of the debt position to be processed.
     * @return the minimum due date ({@link OffsetDateTime}) among all unpaid installments
     */
    LocalDate checkAndUpdateInstallmentExpiration(Long debtPositionId);
}
