package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

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

        WorkflowStatusDTO workflowStatusDTO = new WorkflowStatusDTO();
        workflowStatusDTO.setStatus(WORKFLOW_EXECUTION_STATUS_COMPLETED);

        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion(WORKFLOW_ID, maxRetries, retryDelayMs))
                .thenReturn(workflowStatusDTO);

        // When
        List<InstallmentErrorDTO> result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void givenWorkflowIdNullWhenWaitForWorkflowCompletionThenReturnTrue() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();

        // When
        List<InstallmentErrorDTO> result = service.waitForWorkflowCompletion(null, installment);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void givenStatusFailedWhenWaitForWorkflowCompletionThenAddErrorList() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();

        WorkflowStatusDTO workflowStatusDTO = new WorkflowStatusDTO();
        workflowStatusDTO.setStatus(WORKFLOW_EXECUTION_STATUS_FAILED);

        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion(WORKFLOW_ID, maxRetries, retryDelayMs))
                .thenReturn(workflowStatusDTO);

        // When
        List<InstallmentErrorDTO> result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(FileErrorCode.WORKFLOW_TERMINATED_WITH_FAILURE.name(), result.getFirst().getErrorCode());
        assertEquals(FileErrorCode.WORKFLOW_TERMINATED_WITH_FAILURE.getMessage(), result.getFirst().getErrorMessage());
    }

    @Test
    void givenRetryReachedLimitWhenWaitForWorkflowCompletionThenCatchTooManyAttemptsExceptionAndAddError() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();

        Mockito.doThrow(new RestClientException("Error"))
                .when(workflowHubServiceMock).waitWorkflowCompletion(WORKFLOW_ID, maxRetries, retryDelayMs);

        // When
        List<InstallmentErrorDTO> result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(FileErrorCode.WORKFLOW_TIMEOUT.name(), result.getFirst().getErrorCode());
        assertEquals(FileErrorCode.WORKFLOW_TIMEOUT.getMessage(), result.getFirst().getErrorMessage());
    }
}
