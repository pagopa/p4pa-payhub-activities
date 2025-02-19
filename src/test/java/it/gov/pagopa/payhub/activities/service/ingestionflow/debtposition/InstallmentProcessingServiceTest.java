package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition.mapper.InstallmentSynchronizeMapper;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.WorkflowHubService;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowStatusDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.dto.debtposition.constants.WorkflowStatus.COMPLETED;
import static it.gov.pagopa.payhub.activities.dto.debtposition.constants.WorkflowStatus.FAILED;
import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildInstallmentIngestionFlowFileDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstallmentProcessingServiceTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private WorkflowHubService workflowHubServiceMock;
    @Mock
    private InstallmentSynchronizeMapper installmentSynchronizeMapperMock;
    @Mock
    private InstallmentErrorsArchiverService installmentErrorsArchiverServiceMock;

    private InstallmentProcessingService service;

    @BeforeEach
    void setUp(){
        service = new InstallmentProcessingService(
                debtPositionServiceMock,
                workflowHubServiceMock,
                installmentSynchronizeMapperMock,
                installmentErrorsArchiverServiceMock,
                3,
                100);
    }

    @Test
    void givenProcessInstallmentsThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        String workflowId = "workflow-123";

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(installmentSynchronizeDTO, false))
                .thenReturn(workflowId);

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(workflowId))
                .thenReturn(new WorkflowStatusDTO().status(COMPLETED));

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO),
                ingestionFlowFile,
                Path.of("/tmp"),
                1
        );

        // Then
        assertEquals(1, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertNull(result.getErrorDescription());
        assertNull(result.getDiscardedFileName());
    }

    @Test
    void givenProcessInstallmentsWhenWorkflowIdNullThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(installmentSynchronizeDTO, false))
                .thenReturn(null);

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO),
                ingestionFlowFile,
                Path.of("/tmp"),
                1
        );

        // Then
        assertEquals(1, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertNull(result.getErrorDescription());
        assertNull(result.getDiscardedFileName());
    }

    @Test
    void givenProcessInstallmentsWhenThrowExceptionThenAddError() throws URISyntaxException {
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        Path workingDirectory = Path.of(new URI("file:///tmp"));

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.doThrow(new RestClientException("Error in synchronizing the installment"))
                        .when(debtPositionServiceMock).installmentSynchronize(installmentSynchronizeDTO, false);

        Mockito.when(installmentErrorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName");

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO),
                ingestionFlowFile,
                Path.of("/tmp"),
                1
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName", result.getDiscardedFileName());
        assertEquals(workingDirectory.getParent().toString(), result.getDiscardedFilePath());

        ArgumentCaptor<List<InstallmentErrorDTO>> errorListCaptor = ArgumentCaptor.forClass(List.class);
        verify(installmentErrorsArchiverServiceMock).writeErrors(eq(workingDirectory), any(IngestionFlowFile.class), errorListCaptor.capture());

        List<InstallmentErrorDTO> capturedErrors = errorListCaptor.getValue();
        assertEquals("PROCESS_EXCEPTION", capturedErrors.getFirst().getErrorCode());
        assertEquals("Error in synchronizing the installment", capturedErrors.getFirst().getErrorMessage());
    }

    @Test
    void givenProcessInstallmentsWhenStatusFailedThenAddError() throws URISyntaxException {
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        Path workingDirectory = Path.of(new URI("file:///tmp"));
        String workflowId = "workflow-123";

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(installmentSynchronizeDTO, false))
                .thenReturn(workflowId);

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(workflowId))
                .thenReturn(new WorkflowStatusDTO().status(FAILED));

        Mockito.when(installmentErrorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName");

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO),
                ingestionFlowFile,
                Path.of("/tmp"),
                1
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName", result.getDiscardedFileName());
        assertEquals(workingDirectory.getParent().toString(), result.getDiscardedFilePath());

        ArgumentCaptor<List<InstallmentErrorDTO>> errorListCaptor = ArgumentCaptor.forClass(List.class);
        verify(installmentErrorsArchiverServiceMock).writeErrors(eq(workingDirectory), any(IngestionFlowFile.class), errorListCaptor.capture());

        List<InstallmentErrorDTO> capturedErrors = errorListCaptor.getValue();
        assertEquals(FAILED, capturedErrors.getFirst().getWorkflowStatus());
        assertEquals("WORKFLOW_TERMINATED_WITH_FAILURE", capturedErrors.getFirst().getErrorCode());
        assertEquals("Workflow terminated with error status", capturedErrors.getFirst().getErrorMessage());
    }

    @Test
    void givenProcessInstallmentsWhenRetryLimitReachedThenAddError() throws URISyntaxException {
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        Path workingDirectory = Path.of(new URI("file:///tmp"));
        String workflowId = "workflow-123";
        
        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(installmentSynchronizeDTO, false))
                .thenReturn(workflowId);

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(workflowId))
                .thenReturn(new WorkflowStatusDTO().status(null));

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(workflowId))
                .thenReturn(new WorkflowStatusDTO().status("RUNNING"));

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(workflowId))
                .thenReturn(new WorkflowStatusDTO().status("RUNNING"));

        Mockito.when(installmentErrorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName");

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO),
                ingestionFlowFile,
                Path.of("/tmp"),
                1
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName", result.getDiscardedFileName());
        assertEquals(workingDirectory.getParent().toString(), result.getDiscardedFilePath());

        ArgumentCaptor<List<InstallmentErrorDTO>> errorListCaptor = ArgumentCaptor.forClass(List.class);
        verify(installmentErrorsArchiverServiceMock).writeErrors(eq(workingDirectory), any(IngestionFlowFile.class), errorListCaptor.capture());

        List<InstallmentErrorDTO> capturedErrors = errorListCaptor.getValue();
        assertEquals("RUNNING", capturedErrors.getFirst().getWorkflowStatus());
        assertEquals("RETRY_LIMIT_REACHED", capturedErrors.getFirst().getErrorCode());
        assertEquals("Maximum number of retries reached", capturedErrors.getFirst().getErrorMessage());
    }

    @Test
    void givenProcessInstallmentsWhenThrowInterruptedExceptionThenRetry() {
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        String workflowId = "workflow-123";

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(installmentSynchronizeDTO, false))
                .thenReturn(workflowId);

        Mockito.when(workflowHubServiceMock.getWorkflowStatus(workflowId))
                .thenAnswer(invocation -> {
                    Thread.currentThread().interrupt();
                    return new WorkflowStatusDTO().status("RUNNING");
                })
                .thenReturn(new WorkflowStatusDTO().status("COMPLETED"));

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO),
                ingestionFlowFile,
                Path.of("/tmp"),
                1
        );

        // Then
        assertEquals(1, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertNull(result.getErrorDescription());
        assertNull(result.getDiscardedFileName());
    }

}
