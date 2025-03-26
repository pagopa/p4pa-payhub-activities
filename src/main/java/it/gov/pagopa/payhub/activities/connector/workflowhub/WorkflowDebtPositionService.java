package it.gov.pagopa.payhub.activities.connector.workflowhub;

import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;

/**
 * This interface provides methods to manage workflow for debt position.
 */
public interface WorkflowDebtPositionService {

    /**
     * Synchronize a debt position creating a new workflow
     * @param debtPositionDTO the debt position to synchronize
     * @param wfExecutionParameters wf execution parameters
     * @param paymentEventType the event type for publication into Kafka queue
     * @return {@link WorkflowCreatedDTO} the workflow created for synchronization
     */
    WorkflowCreatedDTO syncDebtPosition(DebtPositionDTO debtPositionDTO, WfExecutionParameters wfExecutionParameters, PaymentEventType paymentEventType, String eventDescription);
}
