package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * This interface provides methods that manage debt positions within the related microservice.
 */
public interface DebtPositionService {

    /**
     * Finalizes the update of the debt position status from the installments.
     *
     * @param debtPositionId the identifier of the debt position to be updated.
     * @param syncStatusUpdateDTO the map of IUD and {@link IupdSyncStatusUpdateDTO} containing new status and IUPD PagoPa of installment.
     * @return the updated {@link DebtPositionDTO} object.
     */
    DebtPositionDTO finalizeSyncStatus(Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusUpdateDTO);

    /**
     * Checks and updates the expiration date of the installments associated with a debt position.
     *
     * @param debtPositionId the unique identifier of the debt position to be processed.
     * @return the minimum due date ({@link OffsetDateTime}) among all unpaid installments.
     */
    OffsetDateTime checkAndUpdateInstallmentExpiration(Long debtPositionId);

    /**
     * Synchronizes an installment from a file.
     *
     * @param installmentSynchronizeDTO the DTO containing installment data to be synchronized.
     * @param massive a flag indicating whether the synchronization is a bulk operation.
     * @return the workflow ID if a workflow is triggered, otherwise null.
     */
    String installmentSynchronize(String origin, InstallmentSynchronizeDTO installmentSynchronizeDTO, Boolean massive);
}
