package it.gov.pagopa.payhub.activities.service;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.TooManyAttemptsException;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class WorkflowCompletionServiceTest {

    @Mock
    private WorkflowHubService workflowHubServiceMock;

    private WorkflowCompletionService service;

    private static final String WORKFLOW_ID = "workflow-123";

    @BeforeEach
    void setUp() {
        service = new WorkflowCompletionService(workflowHubServiceMock);
    }

    @Test
    void givenWaitTerminationStatusThenSuccess() throws TooManyAttemptsException {
        // Given
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_COMPLETED.name()));

        // When
        WorkflowExecutionStatus result = service.waitTerminationStatus(WORKFLOW_ID, 3, 100);

        // Then
        assertEquals(WORKFLOW_EXECUTION_STATUS_COMPLETED, result);
    }

    @Test
    void givenWaitTerminationStatusWhenStatusFailedThenTerminate() throws TooManyAttemptsException {
        // Given
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_FAILED.name()));

        // When
        WorkflowExecutionStatus result = service.waitTerminationStatus(WORKFLOW_ID, 3, 100);

        // Then
        assertEquals(WORKFLOW_EXECUTION_STATUS_FAILED, result);
    }

    @Test
    void givenWaitTerminationStatusWhenStatusNotTerminalThenRetryAndComplete() throws TooManyAttemptsException {
        // Given
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_RUNNING.name()));
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_RUNNING.name()));
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_COMPLETED.name()));

        // When
        WorkflowExecutionStatus result = service.waitTerminationStatus(WORKFLOW_ID, 3, 100);

        // Then
        assertEquals(WORKFLOW_EXECUTION_STATUS_COMPLETED, result);
    }

    @Test
    void givenWaitTerminationStatusWhenThreadInterruptedThenRestoreThreadAndTerminate() throws TooManyAttemptsException {
        // Given
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenAnswer(invocation -> {
                    Thread.currentThread().interrupt();
                    return new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_RUNNING.name());
                })
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_COMPLETED.name()));

        // When
        WorkflowExecutionStatus result = service.waitTerminationStatus(WORKFLOW_ID, 3, 100);

        // Then
        assertEquals(WORKFLOW_EXECUTION_STATUS_COMPLETED, result);
    }

    @Test
    void givenWaitTerminationStatusWhenUnknownStatusThenThrowsTooManyAttemptsException() {
        // Given
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status("RUNNING"));

        // When & Then
        TooManyAttemptsException exception = assertThrows(TooManyAttemptsException.class, () ->
                service.waitTerminationStatus(WORKFLOW_ID, 1, 100)
        );

        assertEquals("Maximum number of retries reached for workflow " + WORKFLOW_ID, exception.getMessage());
    }

    @Test
    void givenWaitTerminationStatusWhenStatusNullThenRetry() {
        // Given
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(null));

        // When & Then
        TooManyAttemptsException exception = assertThrows(TooManyAttemptsException.class, () ->
                service.waitTerminationStatus(WORKFLOW_ID, 1, 100)
        );

        assertEquals("Maximum number of retries reached for workflow " + WORKFLOW_ID, exception.getMessage());
    }
}
