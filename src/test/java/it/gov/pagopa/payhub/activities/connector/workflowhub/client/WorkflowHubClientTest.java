package it.gov.pagopa.payhub.activities.connector.workflowhub.client;

import it.gov.pagopa.payhub.activities.connector.workflowhub.config.WorkflowHubApisHolder;
import it.gov.pagopa.pu.workflowhub.controller.generated.WorkflowApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowHubClientTest {

    @Mock
    private WorkflowHubApisHolder workflowHubApisHolder;
    @Mock
    private WorkflowApi workflowApi;

    private WorkflowHubClient client;

    @BeforeEach
    void setUp() {
        client = new WorkflowHubClient(workflowHubApisHolder);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                workflowHubApisHolder
        );
    }

    @Test
    void whenGetWorkflowStatusThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String workflowId = "workflowId";

        Mockito.when(workflowHubApisHolder.getWorkflowHubApi(accessToken))
                .thenReturn(workflowApi);

        // When
        client.getWorkflowStatus(accessToken, workflowId);

        // Then
        Mockito.verify(workflowApi)
                .getWorkflowStatus(workflowId);
    }

    @Test
    void whenWaitWorkflowCompletionThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String workflowId = "workflowId";
        Integer maxAttempts = 2;
        Integer retryDelayMs = 1;

        Mockito.when(workflowHubApisHolder.getWorkflowHubApi(accessToken))
                .thenReturn(workflowApi);

        // When
        client.waitWorkflowCompletion(accessToken, workflowId, maxAttempts, retryDelayMs);

        // Then
        Mockito.verify(workflowApi).waitWorkflowCompletion(workflowId, maxAttempts, retryDelayMs);
    }
}
