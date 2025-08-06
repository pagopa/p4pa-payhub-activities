package it.gov.pagopa.payhub.activities.connector.workflowhub.client;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.workflowhub.config.WorkflowHubApisHolder;
import it.gov.pagopa.pu.workflowhub.controller.generated.WorkflowApi;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowHubClientTest {

    @Mock
    private WorkflowHubApisHolder workflowHubApisHolderMock;
    @Mock
    private WorkflowApi workflowApiMock;

    private WorkflowHubClient client;

    @BeforeEach
    void setUp() {
        client = new WorkflowHubClient(workflowHubApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                workflowHubApisHolderMock,
                workflowApiMock
        );
    }

    @Test
    void whenGetWorkflowStatusThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String workflowId = "workflowId";

        Mockito.when(workflowHubApisHolderMock.getWorkflowHubApi(accessToken))
                .thenReturn(workflowApiMock);

        WorkflowStatusDTO expectedResult = new WorkflowStatusDTO();
        Mockito.when(workflowApiMock.getWorkflowStatus(workflowId))
                .thenReturn(expectedResult);

        // When
        WorkflowStatusDTO result = client.getWorkflowStatus(accessToken, workflowId);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenWaitWorkflowCompletionThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String workflowId = "workflowId";
        Integer maxAttempts = 2;
        Integer retryDelayMs = 1;

        Mockito.when(workflowHubApisHolderMock.getWorkflowHubApi(accessToken))
                .thenReturn(workflowApiMock);
        WorkflowStatusDTO expectedResult = new WorkflowStatusDTO().status(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);
        Mockito.when(workflowApiMock.waitWorkflowCompletion(workflowId, maxAttempts, retryDelayMs))
                .thenReturn(expectedResult);

        // When
        WorkflowExecutionStatus result = client.waitWorkflowCompletion(accessToken, workflowId, maxAttempts, retryDelayMs);

        // Then
        Assertions.assertSame(expectedResult.getStatus(), result);
    }
}
