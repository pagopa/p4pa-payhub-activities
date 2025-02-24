package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static io.temporal.api.enums.v1.WorkflowExecutionStatus.*;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildInstallmentIngestionFlowFileDTO;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkflowCompletionServiceTest {

    @Mock
    private WorkflowHubService workflowHubServiceMock;

    private WorkflowCompletionService service;

    private static final String WORKFLOW_ID = "workflow-123";
    private static final String FILE_NAME = "fileName";
    private static final String ERROR_CODE = "ERROR_CODE";
    private static final String ERROR_MESSAGE = "Error message";

    @BeforeEach
    void setUp() {
        service = new WorkflowCompletionService(
                workflowHubServiceMock,
                0.005,
                100
        );
    }

    @Test
    void givenWaitForWorkflowCompletionThenSuccess() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_COMPLETED.name()));

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, FILE_NAME, errorList);

        // Then
        assertTrue(result, "Workflow succeeded");
        assertTrue(errorList.isEmpty(), "Error list is empty");
    }

    @Test
    void givenWaitForWorkflowCompletionWhenStatusFailedThenRetry() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_FAILED.name()));

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, FILE_NAME, errorList);

        // Then
        assertFalse(result);
        assertNotNull(errorList);
        assertEquals(WORKFLOW_EXECUTION_STATUS_FAILED.name(), errorList.getFirst().getWorkflowStatus());
        assertEquals("WORKFLOW_TERMINATED_WITH_FAILURE", errorList.getFirst().getErrorCode());
        assertEquals("Workflow terminated with error status", errorList.getFirst().getErrorMessage());
    }

    @Test
    void givenWaitForWorkflowCompletionWhenStatusNotTerminalThenRetry() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_RUNNING.name()));
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_RUNNING.name()));
        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_COMPLETED.name()));

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, FILE_NAME, errorList);

        // Then
        assertTrue(result, "Workflow succeeded");
        assertTrue(errorList.isEmpty(), "Error list is empty");
    }

    @Test
    void givenWaitForWorkflowCompletionWhenThreadInterruptedThenSuccess() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenAnswer(invocation -> {
                    Thread.currentThread().interrupt();
                    return new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_RUNNING.name());
                })
                .thenReturn(new WorkflowStatusDTO().status(WORKFLOW_EXECUTION_STATUS_COMPLETED.name()));

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, FILE_NAME, errorList);

        // Then
        assertTrue(result, "Workflow succeeded");
        assertTrue(errorList.isEmpty(), "Error list is empty");
    }

    @Test
    void givenWaitForWorkflowCompletionWhenRetryReachedLimitThenAddError() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO().status("RUNNING"));

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, FILE_NAME, errorList);

        // Then
        assertFalse(result);
        assertEquals(1, errorList.size());
        assertEquals("RETRY_LIMIT_REACHED", errorList.getFirst().getErrorCode());
        assertEquals("RUNNING", errorList.getFirst().getWorkflowStatus());
        assertEquals("Maximum number of retries reached", errorList.getFirst().getErrorMessage());
    }

    @Test
    void givenWaitForWorkflowCompletionWhenStatusNullThenAddError() {
        // Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();
        List<InstallmentErrorDTO> errorList = new ArrayList<>();

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(WORKFLOW_ID))
                .thenReturn(new WorkflowStatusDTO());

        // When
        boolean result = service.waitForWorkflowCompletion(WORKFLOW_ID, installment, FILE_NAME, errorList);

        // Then
        assertFalse(result);
        assertEquals(1, errorList.size());
        assertEquals("RETRY_LIMIT_REACHED", errorList.getFirst().getErrorCode());
        assertNull(errorList.getFirst().getWorkflowStatus());
        assertEquals("Maximum number of retries reached", errorList.getFirst().getErrorMessage());
    }



    @Test
    void givenHandleProcessingErrorThenOk() {
        //Given
        InstallmentIngestionFlowFileDTO installment = buildInstallmentIngestionFlowFileDTO();

        // When
        InstallmentErrorDTO installmentErrorDTO = service.buildInstallmentErrorDTO(FILE_NAME, installment, WORKFLOW_EXECUTION_STATUS_COMPLETED.name(), ERROR_CODE, ERROR_MESSAGE);

        // Then
        assertEquals(FILE_NAME, installmentErrorDTO.getFileName());
        assertEquals(installment.getIupdOrg(), installmentErrorDTO.getIupdOrg());
        assertEquals(installment.getIud(), installmentErrorDTO.getIud());
        assertEquals(WORKFLOW_EXECUTION_STATUS_COMPLETED.name(), installmentErrorDTO.getWorkflowStatus());
        assertEquals(installment.getIngestionFlowFileLineNumber(), installmentErrorDTO.getRowNumber());
        assertEquals(ERROR_CODE, installmentErrorDTO.getErrorCode());
        assertEquals(ERROR_MESSAGE, installmentErrorDTO.getErrorMessage());
    }
}
