package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileResult;
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

    @Test
    void givenProcessInstallmentsThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        String workflowId = "workflow-123";

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize("ORDINARY_SIL", installmentSynchronizeDTO, false))
                .thenReturn(workflowId);

        Mockito.when(dpInstallmentsWorkflowCompletionServiceMock.waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, ingestionFlowFile.getFileName(), List.of()))
                .thenReturn(true);

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(),
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
    void givenProcessInstallmentsWhenWorkflowNotCompletedThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        String workflowId = "workflow-123";

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize("ORDINARY_SIL", installmentSynchronizeDTO, false))
                .thenReturn(workflowId);

        Mockito.when(dpInstallmentsWorkflowCompletionServiceMock.waitForWorkflowCompletion(workflowId, installmentIngestionFlowFileDTO, ingestionFlowFile.getFileName(), List.of()))
                .thenReturn(false);

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(),
                ingestionFlowFile,
                Path.of("/tmp")
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
    }

    @Test
    void givenProcessInstallmentsWhenWorkflowIdNullThenSuccess(){
        // Given
        InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO = buildInstallmentIngestionFlowFileDTO();
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

        Mockito.when(installmentSynchronizeMapperMock.map(installmentIngestionFlowFileDTO, 1L, 1L))
                .thenReturn(installmentSynchronizeDTO);

        Mockito.when(debtPositionServiceMock.installmentSynchronize("ORDINARY_SIL", installmentSynchronizeDTO, false))
                .thenReturn(null);

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(),
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
                        .when(debtPositionServiceMock).installmentSynchronize("ORDINARY_SIL", installmentSynchronizeDTO, false);

        Mockito.when(dpInstallmentsWorkflowCompletionServiceMock.buildInstallmentErrorDTO(ingestionFlowFile.getFileName(), installmentIngestionFlowFileDTO, null,"PROCESS_EXCEPTION", "Error synchronizing the installment"))
                        .thenReturn(error);

        Mockito.when(installmentErrorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        InstallmentIngestionFlowFileResult result = service.processInstallments(
                Stream.of(installmentIngestionFlowFileDTO).iterator(),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(0, result.getProcessedRows());
        assertEquals(1, result.getTotalRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
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
