package it.gov.pagopa.payhub.activities.connector.workflowhub;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.client.WorkflowDebtPositionClient;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class WorkflowDebtPositionServiceImpl implements WorkflowDebtPositionService {

    private final AuthnService authnService;
    private final WorkflowDebtPositionClient workflowDebtPositionClient;

    public WorkflowDebtPositionServiceImpl(AuthnService authnService, WorkflowDebtPositionClient workflowDebtPositionClient) {
        this.authnService = authnService;
        this.workflowDebtPositionClient = workflowDebtPositionClient;
    }

    @Override
    public WorkflowCreatedDTO syncDebtPosition(DebtPositionDTO debtPositionDTO, WfExecutionParameters wfExecutionParameters, PaymentEventType paymentEventType) {
        String accessToken = authnService.getAccessToken();
        return workflowDebtPositionClient.syncDebtPosition(debtPositionDTO, wfExecutionParameters, paymentEventType, accessToken);
    }
}
