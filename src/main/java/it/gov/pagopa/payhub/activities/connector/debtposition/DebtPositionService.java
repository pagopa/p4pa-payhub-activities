package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.pu.debtposition.dto.generated.*;

import java.time.LocalDate;
import java.util.List;
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
     * @return the minimum due date ({@link LocalDate}) among all unpaid installments.
     */
    LocalDate checkAndUpdateInstallmentExpiration(Long debtPositionId);

    /**
     * Synchronizes an installment from a file.
     *
     * @param installmentSynchronizeDTO the DTO containing installment data to be synchronized.
     * @param wfExecutionParameters wf execution config
     * @return the workflow ID if a workflow is triggered, otherwise null.
     */
    String installmentSynchronize(DebtPositionOrigin origin, InstallmentSynchronizeDTO installmentSynchronizeDTO, WfExecutionParameters wfExecutionParameters, String operatorUserId);

    PagedDebtPositions getDebtPositionsByIngestionFlowFileId(Long ingestionFlowFileId, Integer page, Integer size, List<String> sort);

    /**
     * Update the notification date for the installment that matches the given nav input and is not in a CANCELLED state.
     *
     * @param updateInstallmentNotificationDateRequest the DTO containing installment data to be synchronized.
     */
    void updateInstallmentNotificationDate(UpdateInstallmentNotificationDateRequest updateInstallmentNotificationDateRequest);
}
