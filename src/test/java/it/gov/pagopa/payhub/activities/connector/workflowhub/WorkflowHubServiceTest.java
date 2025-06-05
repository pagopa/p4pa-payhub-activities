package it.gov.pagopa.payhub.activities.connector.workflowhub;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.client.WorkflowHubClient;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WorkflowHubServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private WorkflowHubClient workflowHubClientMock;

    private WorkflowHubService service;

    @BeforeEach
    void setUp() {
        service = new WorkflowHubServiceImpl(authnServiceMock, workflowHubClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                workflowHubClientMock,
                authnServiceMock);
    }

    @Test
    void givenGetWorkflowStatusThenOk() {
        String accessToken = "accessToken";
        String workflowId = "workflowId";
        WorkflowStatusDTO wfStatus = WorkflowStatusDTO.builder()
                .workflowId(workflowId)
                .workflowType("WFTYPE")
                .runId("RUNID")
                .taskQueue("TASKQUEUE")
                .startDateTime(OffsetDateTime.now())
                .executionDateTime(OffsetDateTime.now().plusMinutes(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .duration("PT0S")
                .status(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED)
                .build();

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

        Mockito.when(workflowHubClientMock.getWorkflowStatus(accessToken, workflowId))
                .thenReturn(wfStatus);

        WorkflowStatusDTO workflowStatusDTO = service.getWorkflowStatus(workflowId);

        assertEquals(workflowId, workflowStatusDTO.getWorkflowId());
        assertEquals(wfStatus.getStatus(), workflowStatusDTO.getStatus());
    }

    @Test
    void givenWaitWorkflowCompletionThenOk() {
        String accessToken = "accessToken";
        String workflowId = "workflowId";
        Integer maxAttempts = 2;
        Integer retryDelayMs = 1;

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

        Mockito.when(workflowHubClientMock.waitWorkflowCompletion(accessToken, workflowId, maxAttempts, retryDelayMs))
                .thenReturn(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

        WorkflowExecutionStatus result = service.waitWorkflowCompletion(workflowId, maxAttempts, retryDelayMs);

        assertEquals(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED, result);
    }
}
