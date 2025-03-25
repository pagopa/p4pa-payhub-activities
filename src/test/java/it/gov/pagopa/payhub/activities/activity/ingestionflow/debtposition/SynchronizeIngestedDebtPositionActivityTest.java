package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowDebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.TooManyAttemptsException;
import it.gov.pagopa.payhub.activities.service.WorkflowCompletionService;
import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionOperationTypeResolver;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class SynchronizeIngestedDebtPositionActivityTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private WorkflowDebtPositionService workflowDebtPositionServiceMock;
    @Mock
    private WorkflowCompletionService workflowCompletionServiceMock;
    @Mock
    private DebtPositionOperationTypeResolver debtPositionOperationTypeResolverMock;

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
                debtPositionOperationTypeResolverMock, PAGE_SIZE, MAX_WAITING_MINUTES, RETRY_DELAY
        );
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithoutErrors() throws TooManyAttemptsException {
        Long ingestionFlowFileId = 1L;
        WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();

        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.UNPAID).build());
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.INVALID).build());
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.DRAFT).syncStatusTo(InstallmentStatus.UNPAID).build());

        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO1 = IupdSyncStatusUpdateDTO.builder().newStatus(InstallmentStatus.CANCELLED).build();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO2 = IupdSyncStatusUpdateDTO.builder().newStatus(InstallmentStatus.UNPAID).build();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO3 = IupdSyncStatusUpdateDTO.builder().newStatus(InstallmentStatus.INVALID).build();
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO4 = IupdSyncStatusUpdateDTO.builder().newStatus(InstallmentStatus.UNPAID).build();

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

        Mockito.when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPosition1, Map.of("iud", iupdSyncStatusUpdateDTO1)))
                .thenReturn(PaymentEventType.DP_CANCELLED);
        Mockito.when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPosition2, Map.of("iud", iupdSyncStatusUpdateDTO2)))
                .thenReturn(PaymentEventType.DP_UPDATED);
        Mockito.when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPosition3, Map.of("iud", iupdSyncStatusUpdateDTO3)))
                .thenReturn(PaymentEventType.DP_UPDATED);
        Mockito.when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(debtPosition4, Map.of("iud", iupdSyncStatusUpdateDTO4)))
                .thenReturn(PaymentEventType.DP_CREATED);

        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition1, wfExecutionParameters, PaymentEventType.DP_CANCELLED,"ingestionFlowFileId:1"))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_1").build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_2").build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_3").build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, wfExecutionParameters, PaymentEventType.DP_CREATED,"ingestionFlowFileId:1"))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_4").build());

        Mockito.when(workflowCompletionServiceMock.waitTerminationStatus(anyString(), eq(MAX_ATTEMPS), eq(RETRY_DELAY)))
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
        PaymentEventType paymentEventType = PaymentEventType.DP_CREATED;
        WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();

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

        Mockito.when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(any(), any()))
                .thenReturn(paymentEventType);

        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition1, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(WorkflowCreatedDTO.builder().build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_2").build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(WorkflowCreatedDTO.builder().workflowId("workflowId_3").build());
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(null);

        Mockito.doThrow(new TooManyAttemptsException("Error")).when(workflowCompletionServiceMock)
                .waitTerminationStatus("workflowId_2", MAX_ATTEMPS, RETRY_DELAY);
        Mockito.when(workflowCompletionServiceMock.waitTerminationStatus("workflowId_3", MAX_ATTEMPS, RETRY_DELAY))
                .thenReturn(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TIMED_OUT);

        String result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals("\nError on debt position with iupdOrg " + debtPosition2.getIupdOrg() +": Error" +
                "\nSynchronization workflow for debt position with iupdOrg " + debtPosition3.getIupdOrg() + " terminated with error status.", result);
    }
}
