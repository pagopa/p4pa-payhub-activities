package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED;
import static io.temporal.api.enums.v1.WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_FAILED;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildInstallmentIngestionFlowFileDTO;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DPInstallmentsWorkflowCompletionServiceTest {

    @Mock
    private WorkflowHubService workflowHubServiceMock;

    private DPInstallmentsWorkflowCompletionService service;

    private static final String WORKFLOW_ID = "workflow-123";
    private static final String FILE_NAME = "fileName";
    private int retryDelayMs;
    private int maxRetries;

    @BeforeEach
    void setUp() {
        int maxWaitingMinutes = 1;
        retryDelayMs = 10;
        maxRetries = (int) (((double) maxWaitingMinutes * 60_000) / retryDelayMs);
        service = new DPInstallmentsWorkflowCompletionService(
                workflowHubServiceMock,
                maxWaitingMinutes,
                retryDelayMs
        );
    }

    @Test
    void whenWaitForWorkflowCompletionThenSuccess() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion(WORKFLOW_ID, maxRetries, retryDelayMs))
                .thenReturn(WORKFLOW_EXECUTION_STATUS_COMPLETED);

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, 1L, FILE_NAME, errorList);

        // Then
        assertTrue(result, "Workflow succeeded");
        assertTrue(errorList.isEmpty(), "Error list is empty");
    }

    @Test
    void givenWorkflowIdNullWhenWaitForWorkflowCompletionThenReturnTrue() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        // When
        boolean result = service.waitForWorkflowCompletion(null, installment, 1L, FILE_NAME, errorList);

        // Then
        assertTrue(result, "Workflow succeeded");
        assertTrue(errorList.isEmpty(), "Error list is empty");
    }

    @Test
    void givenStatusFailedWhenWaitForWorkflowCompletionThenAddErrorList() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion(WORKFLOW_ID, maxRetries, retryDelayMs))
                .thenReturn(WORKFLOW_EXECUTION_STATUS_FAILED);

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, 1L, FILE_NAME, errorList);

        // Then
        assertFalse(result);
        assertNotNull(errorList);
        assertEquals(WORKFLOW_EXECUTION_STATUS_FAILED.name(), errorList.getFirst().getWorkflowStatus());
        assertEquals("WORKFLOW_TERMINATED_WITH_FAILURE", errorList.getFirst().getErrorCode());
        assertEquals("Workflow terminated with error status", errorList.getFirst().getErrorMessage());
    }

    @Test
    void givenRetryReachedLimitWhenWaitForWorkflowCompletionThenCatchTooManyAttemptsExceptionAndAddError() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.doThrow(new RestClientException("Error"))
                .when(workflowHubServiceMock).waitWorkflowCompletion(WORKFLOW_ID, maxRetries, retryDelayMs);

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, 1L, FILE_NAME, errorList);

        // Then
        assertFalse(result);
        assertEquals(1, errorList.size());
        assertEquals("RETRY_LIMIT_REACHED", errorList.getFirst().getErrorCode());
        assertEquals("Maximum number of retries reached", errorList.getFirst().getErrorMessage());
    }
}
