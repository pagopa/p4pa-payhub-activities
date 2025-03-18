package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowDebtPositionService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.TooManyAttemptsException;
import it.gov.pagopa.payhub.activities.service.WorkflowCompletionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedDebtPositions;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SynchronizeIngestedDebtPositionActivityTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private WorkflowDebtPositionService workflowDebtPositionServiceMock;
    @Mock
    private WorkflowCompletionService workflowCompletionServiceMock;

    private static final Integer PAGE_SIZE = 2;
    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId,asc");
    private static final int MAX_WAITING_MINUTES = 1;
    private static final int RETRY_DELAY = 10;
    private static final int MAX_ATTEMPS = (int) (((double) MAX_WAITING_MINUTES * 60_000) / RETRY_DELAY);


    private SynchronizeIngestedDebtPositionActivity activity;

    @BeforeEach
    void setUp() {
        activity = new SynchronizeIngestedDebtPositionActivityImpl(
                debtPositionServiceMock, workflowDebtPositionServiceMock, workflowCompletionServiceMock,
                PAGE_SIZE, MAX_WAITING_MINUTES, RETRY_DELAY
        );
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithoutErrors() throws TooManyAttemptsException {
        Long ingestionFlowFileId = 1L;
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();
        WorkflowExecutionStatus workflowExecutionStatus = WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED;

        PagedDebtPositions pagedDebtPositionsFirstPage = PagedDebtPositions.builder()
                .content(List.of(debtPosition1, debtPosition2))
                .size(2L)
                .totalPages(2L)
                .totalElements(4L)
                .number(0L)
                .build();

        PagedDebtPositions pagedDebtPositionsSecondPage = PagedDebtPositions.builder()
                .content(List.of(debtPosition3, debtPosition4))
                .size(2L)
                .totalPages(2L)
                .totalElements(4L)
                .number(1L)
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 1, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsSecondPage);
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(Mockito.any(DebtPositionDTO.class), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId").build());
        Mockito.when(workflowCompletionServiceMock.waitTerminationStatus("workflowId", MAX_ATTEMPS, RETRY_DELAY))
                .thenReturn(workflowExecutionStatus);

        String result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals("", result);
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithErrors() throws TooManyAttemptsException {
        Long ingestionFlowFileId = 1L;
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();

        PagedDebtPositions pagedDebtPositionsFirstPage = PagedDebtPositions.builder()
                .content(List.of(debtPosition1, debtPosition2))
                .size(2L)
                .totalPages(2L)
                .totalElements(4L)
                .number(0L)
                .build();

        PagedDebtPositions pagedDebtPositionsSecondPage = PagedDebtPositions.builder()
                .content(List.of(debtPosition3, debtPosition4))
                .size(2L)
                .totalPages(2L)
                .totalElements(4L)
                .number(1L)
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, 1, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsSecondPage);

        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition1, false, null))
                .thenReturn(WorkflowCreatedDTO.builder().build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, false, null))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_2").build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, false, null))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_3").build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, false, null))
                .thenReturn(null);

        Mockito.when(workflowCompletionServiceMock.waitTerminationStatus("workflowId_2", MAX_ATTEMPS, RETRY_DELAY))
                .thenReturn(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);
        Mockito.when(workflowCompletionServiceMock.waitTerminationStatus("workflowId_3", MAX_ATTEMPS, RETRY_DELAY))
                .thenReturn(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TIMED_OUT);

        String result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals("No synchronization workflow created for debt position with iupdOrg " + debtPosition1.getIupdOrg() + "\n" +
                "Synchronization workflow for debt position with iupdOrg " + debtPosition3.getIupdOrg() + " terminated with error status.\n" +
                "No synchronization workflow created for debt position with iupdOrg " + debtPosition4.getIupdOrg() + "\n", result);
    }
}
