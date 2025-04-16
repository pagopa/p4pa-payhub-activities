package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition.InstallmentSynchronizeMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentIngestionFlowFileDTOFaker.buildInstallmentIngestionFlowFileDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin.ORDINARY_SIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstallmentProcessingServiceTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private InstallmentSynchronizeMapper installmentSynchronizeMapperMock;
    @Mock
    private InstallmentErrorsArchiverService installmentErrorsArchiverServiceMock;
    @Mock
    private DPInstallmentsWorkflowCompletionService dpInstallmentsWorkflowCompletionServiceMock;

    private InstallmentProcessingService service;

    @BeforeEach
    void setUp(){
        service = new InstallmentProcessingService(
                debtPositionServiceMock,
                installmentSynchronizeMapperMock,
                installmentErrorsArchiverServiceMock,
                dpInstallmentsWorkflowCompletionServiceMock
                );
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionServiceMock,
                installmentSynchronizeMapperMock,
                installmentErrorsArchiverServiceMock,
                dpInstallmentsWorkflowCompletionServiceMock);
    }

    @Test
    void whenProcessInstallmentsThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        String workflowId = "workflow-123";
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(true)
                .partialChange(true)
                .build();

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L,1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId()))
                .thenReturn(workflowId);

        Mockito.when(dpInstallmentsWorkflowCompletionServiceMock.waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, 1L, ingestionFlowFile.getFileName(), List.of()))
                .thenReturn(true);

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(), List.of(),
                ingestionFlowFile,
                Path.of("/tmp")
        );

        // Then
        assertEquals(1, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertNull(result.getErrorDescription());
        assertNull(result.getDiscardedFileName());
    }

    @Test
    void givenWorkflowNotCompletedWhenProcessInstallmentsThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        String workflowId = "workflow-123";
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(true)
                .partialChange(true)
                .build();

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId()))
                .thenReturn(workflowId);

        Mockito.when(dpInstallmentsWorkflowCompletionServiceMock.waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, 1L, ingestionFlowFile.getFileName(), List.of()))
                .thenReturn(false);

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(), List.of(),
                ingestionFlowFile,
                Path.of("/tmp")
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
    }

    @Test
    void givenWorkflowIdNullWhenProcessInstallmentsThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(true)
                .partialChange(true)
                .build();

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId()))
                .thenReturn(null);

        Mockito.when(dpInstallmentsWorkflowCompletionServiceMock.waitForWorkflowCompletion(null, installmentIngestionFlowFileDTO, 1L, ingestionFlowFile.getFileName(), List.of()))
                .thenReturn(true);

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(), List.of(),
                ingestionFlowFile,
                Path.of("/tmp")
        );

        // Then
        assertEquals(1, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertNull(result.getErrorDescription());
        assertNull(result.getDiscardedFileName());
    }

    @Test
    void givenThrowExceptionWhenProcessInstallmentsThenAddError() throws URISyntaxException {
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(true)
                .partialChange(true)
                .build();
        Path workingDirectory = Path.of(new URI("file:///tmp"));

                Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 2L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.doThrow(new RestClientException("Error synchronizing the installment"))
                        .when(debtPositionServiceMock).installmentSynchronize(ORDINARY_SIL, installmentSynchronizeDTO, wfExecutionParameters, ingestionFlowFile.getOperatorExternalId());

        Mockito.when(installmentErrorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(2, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        verify(installmentErrorsArchiverServiceMock).writeErrors(eq(workingDirectory), eq(ingestionFlowFile), eq(List.of(
                new InstallmentErrorDTO(ingestionFlowFile.getFileName(), null, null, null, -1L, "READER_EXCEPTION", "DUMMYERROR"),
                new InstallmentErrorDTO(ingestionFlowFile.getFileName(), installmentSynchronizeDTO.getIupdOrg(), installmentSynchronizeDTO.getIud(), null, 2L, "PROCESS_EXCEPTION", "Error synchronizing the installment")
        )));
    }
}
