package it.gov.pagopa.payhub.activities.connector.workflowhub.client;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.workflowhub.config.WorkflowHubApisHolder;
import it.gov.pagopa.pu.workflowhub.client.generated.WorkflowApi;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

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

        when(workflowHubApisHolderMock.getWorkflowHubApi(accessToken))
                .thenReturn(workflowApiMock);

        WorkflowStatusDTO expectedResult = new WorkflowStatusDTO();
        when(workflowApiMock.getWorkflowStatus(workflowId))
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

        when(workflowHubApisHolderMock.getWorkflowHubApi(accessToken))
                .thenReturn(workflowApiMock);
        WorkflowStatusDTO expectedResult = new WorkflowStatusDTO().status(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);
        when(workflowApiMock.waitWorkflowCompletion(workflowId, maxAttempts, retryDelayMs))
                .thenReturn(expectedResult);

        // When
        WorkflowStatusDTO result = client.waitWorkflowCompletion(accessToken, workflowId, maxAttempts, retryDelayMs);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
