package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowDebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.SyncIngestedDebtPositionDTO;
import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionOperationTypeResolver;
import it.gov.pagopa.payhub.activities.service.exportflow.debtposition.IUVArchivingExportFileService;
import it.gov.pagopa.payhub.activities.service.pagopapayments.GenerateNoticeService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.pagopapayments.dto.generated.GeneratedNoticeMassiveFolderDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.nio.file.Path;
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
    private WorkflowHubService workflowHubServiceMock;
    @Mock
    private GenerateNoticeService generateNoticeServiceMock;
    @Mock
    private DebtPositionOperationTypeResolver debtPositionOperationTypeResolverMock;
    @Mock
    private IUVArchivingExportFileService iuvArchivingExportFileServiceMock;

    private ObjectMapper objectMapper = new JsonConfig().objectMapper();

    private static final Integer PAGE_SIZE = 2;
    private static final List<String> DEFAULT_ORDERING = List.of("debtPositionId,asc");
    private static final int MAX_WAITING_MINUTES = 1;
    private static final int RETRY_DELAY = 10;
    private static final int MAX_ATTEMPTS = (int) (((double) MAX_WAITING_MINUTES * 60_000) / RETRY_DELAY);
    private static final List<InstallmentStatus> statusToExclude = List.of(InstallmentStatus.DRAFT);


    private SynchronizeIngestedDebtPositionActivity activity;

    @BeforeEach
    void setUp() {
        activity = new SynchronizeIngestedDebtPositionActivityImpl(
                debtPositionServiceMock, workflowDebtPositionServiceMock, workflowHubServiceMock, generateNoticeServiceMock,
                debtPositionOperationTypeResolverMock, PAGE_SIZE, MAX_WAITING_MINUTES, RETRY_DELAY, iuvArchivingExportFileServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionServiceMock,
                workflowDebtPositionServiceMock,
                workflowHubServiceMock,
                debtPositionOperationTypeResolverMock,
                iuvArchivingExportFileServiceMock
        );
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithoutErrors_NoResult() {
        testSynchronizeIngestedDebtPositionWithoutErrors(false);
    }
    @Test
    void testSynchronizeIngestedDebtPositionWithoutErrors_SuccessfulResult() {
        testSynchronizeIngestedDebtPositionWithoutErrors(true);
    }
    @SneakyThrows
    void testSynchronizeIngestedDebtPositionWithoutErrors(boolean hasWfResult) {
        Long ingestionFlowFileId = 1L;
        WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();

        List<DebtPositionDTO> debtPositionsExportIuv = List.of(debtPosition1, debtPosition2, debtPosition3, debtPosition4);
        List<DebtPositionDTO> debtPositionsGenerateNotices = List.of(debtPosition2, debtPosition3, debtPosition4);
        List<String> iuvListGenerateNotices = List.of("iuv", "iuv", "iuv");

        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.UNPAID).build());
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.INVALID).build());
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.DRAFT).syncStatusTo(InstallmentStatus.UNPAID).build());

        SyncCompleteDTO iupdSyncStatusUpdateDTO1 = new SyncCompleteDTO(InstallmentStatus.CANCELLED);
        SyncCompleteDTO iupdSyncStatusUpdateDTO2 = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        SyncCompleteDTO iupdSyncStatusUpdateDTO3 = new SyncCompleteDTO(InstallmentStatus.INVALID);
        SyncCompleteDTO iupdSyncStatusUpdateDTO4 = new SyncCompleteDTO(InstallmentStatus.UNPAID);

        WorkflowStatusDTO workflowStatusDTO = new WorkflowStatusDTO();
        workflowStatusDTO.setStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);
        if(hasWfResult){
            SyncStatusUpdateRequestDTO wfSyncResult = new SyncStatusUpdateRequestDTO();
            wfSyncResult.setIupd2finalize(Map.of("IUD", new SyncCompleteDTO(InstallmentStatus.UNPAID)));
            workflowStatusDTO.setResult(objectMapper.writeValueAsString(wfSyncResult));
        }

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

        GeneratedNoticeMassiveFolderDTO responseFolder = GeneratedNoticeMassiveFolderDTO.builder()
                .folderId("folderId")
                .build();

        Path path = Path.of("test", "iuv.csv");
        SyncIngestedDebtPositionDTO response = SyncIngestedDebtPositionDTO.builder()
                .pdfGeneratedId(responseFolder.getFolderId())
                .errorsDescription("")
                .iuvFileName(path.getFileName().toString())
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 1, PAGE_SIZE, DEFAULT_ORDERING))
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
                .thenReturn(new WorkflowCreatedDTO("workflowId_1", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_2", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_3", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, wfExecutionParameters, PaymentEventType.DP_CREATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_4", "runId"));

        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion(anyString(), eq(MAX_ATTEMPTS), eq(RETRY_DELAY)))
                .thenReturn(workflowStatusDTO);

        Mockito.when(generateNoticeServiceMock.generateNotices(ingestionFlowFileId, debtPositionsGenerateNotices, iuvListGenerateNotices))
                .thenReturn("folderId");

        Mockito.when(iuvArchivingExportFileServiceMock.executeExport(debtPositionsExportIuv, ingestionFlowFileId))
                .thenReturn(path);

        SyncIngestedDebtPositionDTO result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals(response, result);
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithErrors() {
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

        SyncIngestedDebtPositionDTO response = SyncIngestedDebtPositionDTO.builder()
                .errorsDescription("\nError on debt position with iupdOrg " + debtPosition2.getIupdOrg() +": Error" +
                        "\nSynchronization workflow for debt position with iupdOrg " + debtPosition3.getIupdOrg() + " terminated with error status.")
                .build();

        WorkflowStatusDTO worflowStatusDTO = new WorkflowStatusDTO();
        worflowStatusDTO.setStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TIMED_OUT);

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 1, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsSecondPage);

        Mockito.when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(any(), any()))
                .thenReturn(paymentEventType);

        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition1, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(null);
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_2", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_3", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(null);

        Mockito.doThrow(new RestClientException("Error")).when(workflowHubServiceMock)
                .waitWorkflowCompletion("workflowId_2", MAX_ATTEMPTS, RETRY_DELAY);
        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion("workflowId_3", MAX_ATTEMPTS, RETRY_DELAY))
                .thenReturn(worflowStatusDTO);

        SyncIngestedDebtPositionDTO result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals(response, result);
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithErrorsOnResult() throws JsonProcessingException {
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

        SyncIngestedDebtPositionDTO response = SyncIngestedDebtPositionDTO.builder()
                .errorsDescription("\nError on debt position with iupdOrg " + debtPosition2.getIupdOrg() +": Error" +
                        "\nSynchronization workflow for debt position with iupdOrg " + debtPosition3.getIupdOrg() + " terminated with error status.")
                .build();

        WorkflowStatusDTO worflowStatusDTO = new WorkflowStatusDTO();
        worflowStatusDTO.setStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);
        SyncStatusUpdateRequestDTO syncResult = new SyncStatusUpdateRequestDTO();
        syncResult.setIupdSyncError(Map.of("IUD_ERROR", new SyncErrorDTO("ERROR")));
        worflowStatusDTO.setResult(objectMapper.writeValueAsString(syncResult));

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 1, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsSecondPage);

        Mockito.when(debtPositionOperationTypeResolverMock.calculateDebtPositionOperationType(any(), any()))
                .thenReturn(paymentEventType);

        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition1, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(null);
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_2", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_3", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, wfExecutionParameters, paymentEventType, "ingestionFlowFileId:1"))
                .thenReturn(null);

        Mockito.doThrow(new RestClientException("Error")).when(workflowHubServiceMock)
                .waitWorkflowCompletion("workflowId_2", MAX_ATTEMPTS, RETRY_DELAY);
        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion("workflowId_3", MAX_ATTEMPTS, RETRY_DELAY))
                .thenReturn(worflowStatusDTO);

        SyncIngestedDebtPositionDTO result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals(response, result);
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithErrorsOnSyncAPI() {
        Long ingestionFlowFileId = 1L;
        WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();

        debtPosition1.setFlagPuPagoPaPayment(false);

        List<DebtPositionDTO> debtPositionsExportIuv = List.of(debtPosition3, debtPosition4);
        List<DebtPositionDTO> debtPositionsGenerateNotices = List.of(debtPosition4);
        List<String> iuvListGenerateNotices = List.of("iuv");

        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.UNPAID).build());
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.DRAFT).syncStatusTo(InstallmentStatus.UNPAID).build());

        SyncCompleteDTO iupdSyncStatusUpdateDTO1 = new SyncCompleteDTO(InstallmentStatus.CANCELLED);
        SyncCompleteDTO iupdSyncStatusUpdateDTO2 = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        SyncCompleteDTO iupdSyncStatusUpdateDTO3 = new SyncCompleteDTO(InstallmentStatus.CANCELLED);
        SyncCompleteDTO iupdSyncStatusUpdateDTO4 = new SyncCompleteDTO(InstallmentStatus.UNPAID);

        WorkflowStatusDTO workflowStatusDTO = new WorkflowStatusDTO();
        workflowStatusDTO.setStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

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

        GeneratedNoticeMassiveFolderDTO responseFolder = GeneratedNoticeMassiveFolderDTO.builder()
                .folderId("folderId")
                .build();

        Path path = Path.of("test", "iuv.csv");
        SyncIngestedDebtPositionDTO response = SyncIngestedDebtPositionDTO.builder()
                .pdfGeneratedId(responseFolder.getFolderId())
                .errorsDescription("\nError on debt position with iupdOrg " + debtPosition2.getIupdOrg() +": DUMMYEXCEPTION DP2")
                .iuvFileName(path.getFileName().toString())
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 1, PAGE_SIZE, DEFAULT_ORDERING))
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
                .thenReturn(new WorkflowCreatedDTO("workflowId_1", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenThrow(new RuntimeException("DUMMYEXCEPTION DP2"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_3", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, wfExecutionParameters, PaymentEventType.DP_CREATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_4", "runId"));

        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion(anyString(), eq(MAX_ATTEMPTS), eq(RETRY_DELAY)))
                .thenReturn(workflowStatusDTO);

        Mockito.when(generateNoticeServiceMock.generateNotices(ingestionFlowFileId, debtPositionsGenerateNotices, iuvListGenerateNotices))
                .thenReturn("folderId");
        Mockito.when(iuvArchivingExportFileServiceMock.executeExport(debtPositionsExportIuv, ingestionFlowFileId))
                .thenReturn(path);

        SyncIngestedDebtPositionDTO result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals(response, result);
    }

    @Test
    void testSynchronizeIngestedDebtPositionWithErrorOnGenerateNotices() {
        Long ingestionFlowFileId = 1L;
        WfExecutionParameters wfExecutionParameters = new WfExecutionParameters();
        DebtPositionDTO debtPosition1 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition2 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition3 = buildDebtPositionDTO();
        DebtPositionDTO debtPosition4 = buildDebtPositionDTO();

        List<DebtPositionDTO> debtPositionsExportIuv = List.of(debtPosition1, debtPosition2, debtPosition3, debtPosition4);
        List<DebtPositionDTO> debtPositionsGenerateNotices = List.of(debtPosition2, debtPosition3, debtPosition4);
        List<String> iuvListGenerateNotices = List.of("iuv", "iuv", "iuv");

        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition1.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.CANCELLED).build());
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition2.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.UNPAID).build());
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition3.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.UNPAID).syncStatusTo(InstallmentStatus.INVALID).build());
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setStatus(InstallmentStatus.TO_SYNC);
        debtPosition4.getPaymentOptions().getFirst().getInstallments().getFirst().setSyncStatus(InstallmentSyncStatus.builder().syncStatusFrom(InstallmentStatus.DRAFT).syncStatusTo(InstallmentStatus.UNPAID).build());

        SyncCompleteDTO iupdSyncStatusUpdateDTO1 = new SyncCompleteDTO(InstallmentStatus.CANCELLED);
        SyncCompleteDTO iupdSyncStatusUpdateDTO2 = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        SyncCompleteDTO iupdSyncStatusUpdateDTO3 = new SyncCompleteDTO(InstallmentStatus.INVALID);
        SyncCompleteDTO iupdSyncStatusUpdateDTO4 = new SyncCompleteDTO(InstallmentStatus.UNPAID);

        WorkflowStatusDTO workflowStatusDTO = new WorkflowStatusDTO();
        workflowStatusDTO.setStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

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

        Path path = Path.of("test", "iuv.csv");
        SyncIngestedDebtPositionDTO response = SyncIngestedDebtPositionDTO.builder()
                .pdfGeneratedId(null)
                .errorsDescription("\nError on generate notice massive for ingestionFlowFileId 1: Broker 1 has not GENERATE_NOTICE apiKey configured!")
                .iuvFileName(path.getFileName().toString())
                .build();

        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId,  statusToExclude, 0, PAGE_SIZE, DEFAULT_ORDERING))
                .thenReturn(pagedDebtPositionsFirstPage);
        Mockito.when(debtPositionServiceMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, statusToExclude, 1, PAGE_SIZE, DEFAULT_ORDERING))
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
                .thenReturn(new WorkflowCreatedDTO("workflowId_1", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition2, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_2", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition3, wfExecutionParameters, PaymentEventType.DP_UPDATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_3", "runId"));
        Mockito.when(workflowDebtPositionServiceMock.syncDebtPosition(debtPosition4, wfExecutionParameters, PaymentEventType.DP_CREATED,"ingestionFlowFileId:1"))
                .thenReturn(new WorkflowCreatedDTO("workflowId_4", "runId"));

        Mockito.when(workflowHubServiceMock.waitWorkflowCompletion(anyString(), eq(MAX_ATTEMPTS), eq(RETRY_DELAY)))
                .thenReturn(workflowStatusDTO);

        Mockito.when(generateNoticeServiceMock.generateNotices(ingestionFlowFileId, debtPositionsGenerateNotices, iuvListGenerateNotices))
                .thenThrow(new IllegalStateException("Broker 1 has not GENERATE_NOTICE apiKey configured!"));

        Mockito.when(iuvArchivingExportFileServiceMock.executeExport(debtPositionsExportIuv, ingestionFlowFileId))
                .thenReturn(path);

        SyncIngestedDebtPositionDTO result = activity.synchronizeIngestedDebtPosition(ingestionFlowFileId);

        assertEquals(response, result);
    }
}
