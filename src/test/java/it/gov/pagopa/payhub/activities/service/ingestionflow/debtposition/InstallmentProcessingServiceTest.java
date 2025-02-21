package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition.InstallmentSynchronizeMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class InstallmentProcessingServiceTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private InstallmentSynchronizeMapper installmentSynchronizeMapperMock;
    @Mock
    private InstallmentErrorsArchiverService installmentErrorsArchiverServiceMock;
    @Mock
    private WorkflowCompletionService workflowCompletionServiceMock;

    private InstallmentProcessingService service;

    @BeforeEach
    void setUp(){
        service = new InstallmentProcessingService(
                debtPositionServiceMock,
                installmentSynchronizeMapperMock,
                installmentErrorsArchiverServiceMock,
                workflowCompletionServiceMock
                );
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

        Mockito.when(workflowCompletionServiceMock.waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, ingestionFlowFile.getFileName(), List.of()))
                .thenReturn(true);

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
        InstallmentErrorDTO error = buildInstallmentErrorDTO(installmentIngestionFlowFileDTO);

                Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.doThrow(new RestClientException("Error synchronizing the installment"))
                        .when(debtPositionServiceMock).installmentSynchronize(installmentSynchronizeDTO, false);

        Mockito.when(workflowCompletionServiceMock.buildInstallmentErrorDTO(ingestionFlowFile.getFileName(), installmentIngestionFlowFileDTO, null,"PROCESS_EXCEPTION", "Error synchronizing the installment"))
                        .thenReturn(error);

        Mockito.when(installmentErrorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        Mockito.when(installmentErrorsArchiverServiceMock.createTargetDirectory(ingestionFlowFile))
                .thenReturn(Path.of("/tmp/path"));

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO),
                ingestionFlowFile,
                workingDirectory,
                1
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        assertEquals("\\tmp\\path\\zipFileName.csv", result.getDiscardedFilePath());
    }

    private InstallmentErrorDTO buildInstallmentErrorDTO(InstallmentIngestionFlowFileDTO installment) {
        return InstallmentErrorDTO.builder()
                .fileName("fileName.csv")
                .iupdOrg(installment.getIupdOrg())
                .iud(installment.getIud())
                .workflowStatus(null)
                .rowNumber(installment.getIngestionFlowFileLineNumber())
                .errorCode("PROCESS_EXCEPTION")
                .errorMessage("Error in synchronizing the installment")
                .build();
    }
}
