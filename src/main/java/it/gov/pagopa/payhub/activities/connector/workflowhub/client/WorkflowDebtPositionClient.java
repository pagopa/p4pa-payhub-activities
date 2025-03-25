package it.gov.pagopa.payhub.activities.connector.workflowhub.client;

import it.gov.pagopa.payhub.activities.connector.workflowhub.config.WorkflowHubApisHolder;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.SyncDebtPositionRequestDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class WorkflowDebtPositionClient {

    private final WorkflowHubApisHolder workflowHubApisHolder;

    public WorkflowDebtPositionClient(WorkflowHubApisHolder workflowHubApisHolder) {
        this.workflowHubApisHolder = workflowHubApisHolder;
    }

    public WorkflowCreatedDTO syncDebtPosition(DebtPositionDTO debtPositionDTO, WfExecutionParameters wfExecutionParameters, PaymentEventType paymentEventType, String eventDescription, String accessToken){
        return workflowHubApisHolder.getDebtPositionApi(accessToken)
                .syncDebtPosition(new SyncDebtPositionRequestDTO(debtPositionDTO, wfExecutionParameters.getWfExecutionConfig()), wfExecutionParameters.isMassive(), wfExecutionParameters.isPartialChange(), paymentEventType, eventDescription);
    }

}
