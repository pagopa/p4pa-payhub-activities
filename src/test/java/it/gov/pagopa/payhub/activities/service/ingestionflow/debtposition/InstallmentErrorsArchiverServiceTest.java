package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentErrorDTO;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class InstallmentErrorsArchiverServiceTest {

    private final String errorFolder = "error";
    @Mock
    private IngestionFlowFileArchiverService ingestionFlowFileArchiverServiceMock;
    @Mock
    private CsvService csvServiceMock;

    private InstallmentErrorsArchiverService service;

    private final String sharedDirectory = "/tmp";

    @BeforeEach
    void setUp() {
        service = new InstallmentErrorsArchiverService(sharedDirectory, errorFolder, ingestionFlowFileArchiverServiceMock, csvServiceMock);
    }

    @Test
    void testWriteErrors_whenValidInput_thenCreatesAndArchivesCsv() throws IOException {
        List<InstallmentErrorDTO> errorDTOList = List.of(
                new InstallmentErrorDTO("fileName", "iupdOrg", "iud", "workflowStatus", 1L, "errorCode", "errorMessage"),
                new InstallmentErrorDTO("fileName", "iupdOrg", "iud", "workflowStatus", 1L, "errorCode", "errorMessage")
        );
        Path workingDirectory = Path.of("build", "test");
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        Path expectedErrorFilePath = workingDirectory.resolve("ERROR-fileName.csv");

        // When
        service.writeErrors(workingDirectory, ingestionFlowFileDTO, errorDTOList);

        // Then
        Mockito.verify(csvServiceMock)
                .createCsv(eq(expectedErrorFilePath), any(), any());
    }

    @Test
    void testWriteErrors_whenIOException_thenThrowsActivitiesException() throws IOException {
        List<InstallmentErrorDTO> errorDTOList = List.of(
                new InstallmentErrorDTO("fileName", "iupdOrg", "iud", "workflowStatus", 1L, "errorCode", "errorMessage")
        );
        Path workingDirectory = Path.of("build", "test");
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        Path expectedErrorFilePath = workingDirectory.resolve("ERROR-fileName.csv");

        Mockito.doThrow(new IOException("Error creating CSV"))
                .when(csvServiceMock)
                .createCsv(eq(expectedErrorFilePath), any(), any());

        // When & Then
        NotRetryableActivityException exception = assertThrows(NotRetryableActivityException.class, () ->
                service.writeErrors(workingDirectory, ingestionFlowFileDTO, errorDTOList));
        assertEquals("Error creating CSV", exception.getMessage());
    }

    @Test
    void givenNoErrorsWhenArchiveErrorFilesThenReturnNull() {
        // Given
        Path workingDirectory = Path.of("build");

        // When
        String result = service.archiveErrorFiles(workingDirectory, new IngestionFlowFile());

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void givenErrorsWhenArchiveErrorFilesThenCompressAndArchiveThem() throws IOException {
        // Given
        Path workingDirectory = Path.of("build", "test");
        Files.createDirectories(workingDirectory);
        Path errorFile = Files.createTempFile(workingDirectory, "ERROR-", ".csv");
        try {
            IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
            String expectedZipErrorFileName = "ERROR-fileName.zip";

            // When
            String result = service.archiveErrorFiles(workingDirectory, ingestionFlowFileDTO);

            // Then
            Assertions.assertEquals(expectedZipErrorFileName, result);

            Mockito.verify(ingestionFlowFileArchiverServiceMock)
                    .compressAndArchive(List.of(errorFile), Path.of(expectedZipErrorFileName), Path.of(sharedDirectory, ingestionFlowFileDTO.getOrganizationId() + "", ingestionFlowFileDTO.getFilePathName(), errorFolder));
        } finally {
            Files.delete(errorFile);
        }
    }
}
