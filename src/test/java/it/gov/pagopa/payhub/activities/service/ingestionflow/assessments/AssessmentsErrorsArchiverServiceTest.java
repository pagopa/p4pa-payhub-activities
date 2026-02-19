package it.gov.pagopa.payhub.activities.service.ingestionflow.assessments;

import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
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
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class AssessmentsErrorsArchiverServiceTest {

    @Mock
    private FileArchiverService fileArchiverServiceMock;

    @Mock
    private CsvService csvServiceMock;

    private AssessmentsErrorArchiverService service;

    public static final String FILE_NAME = "fileName";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MESSAGE = "errorMessage";
    private final String errorFolder = "error";
    private final String sharedDirectory = "/tmp";

    @BeforeEach
    void setUp() {
        service = new AssessmentsErrorArchiverService(sharedDirectory, errorFolder, fileArchiverServiceMock, csvServiceMock);
    }

    @Test
    void testWriteErrors_whenValidInput_thenCreatesAndArchivesCsv() throws IOException {
        List<AssessmentsErrorDTO> errorDTOList = List.of(
                new AssessmentsErrorDTO(FILE_NAME, 1L, "code1", "1", ERROR_CODE, ERROR_MESSAGE),
                new AssessmentsErrorDTO(FILE_NAME, 2L, "code2", "1", ERROR_CODE, ERROR_MESSAGE)
        );
        Path workingDirectory = Path.of("build", "test");
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        Path expectedErrorFilePath = workingDirectory.resolve("ERROR-fileName.csv");
        AssessmentsIngestionFlowFileResult result = new AssessmentsIngestionFlowFileResult();

        // When
        service.writeErrors(workingDirectory, ingestionFlowFileDTO, errorDTOList, result);

        // Then
        Mockito.verify(csvServiceMock)
                .createCsv(eq(expectedErrorFilePath), any(), any());
    }

    @Test
    void testWriteErrors_whenErrorListEmpty_thenReturn() throws IOException {
        Path workingDirectory = Path.of("build", "test");
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        Path expectedErrorFilePath = workingDirectory.resolve("ERROR-fileName.csv");
        AssessmentsIngestionFlowFileResult result = new AssessmentsIngestionFlowFileResult();

        // When
        service.writeErrors(workingDirectory, ingestionFlowFileDTO, List.of(), result);

        // Then
        Mockito.verify(csvServiceMock, Mockito.times(0))
                .createCsv(eq(expectedErrorFilePath), any(), any());
    }

    @Test
    void testWriteErrors_whenIOException_thenThrowsActivitiesException() throws IOException {
        List<AssessmentsErrorDTO> errorDTOList = List.of(
                new AssessmentsErrorDTO(FILE_NAME, 1L, "code1", "1", ERROR_CODE, ERROR_MESSAGE)
        );
        Path workingDirectory = Path.of("build", "test");
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        Path expectedErrorFilePath = workingDirectory.resolve("ERROR-fileName.csv");
        AssessmentsIngestionFlowFileResult result = new AssessmentsIngestionFlowFileResult();

        Mockito.doThrow(new IOException("Error creating CSV"))
                .when(csvServiceMock)
                .createCsv(eq(expectedErrorFilePath), any(), any());

        // When & Then
        NotRetryableActivityException exception = assertThrows(NotRetryableActivityException.class, () ->
                service.writeErrors(workingDirectory, ingestionFlowFileDTO, errorDTOList, result));
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

            Mockito.verify(fileArchiverServiceMock)
                    .compressAndArchive(List.of(errorFile), Path.of("build/test/" + expectedZipErrorFileName), Path.of(sharedDirectory, ingestionFlowFileDTO.getOrganizationId() + "", ingestionFlowFileDTO.getFilePathName(), errorFolder));
        } finally {
            Files.delete(errorFile);
        }
    }

    @Test
    void givenArchiveErrorFilesWhenIOExceptionThenReturnNull() throws IOException {
        // Given
        Path workingDirectory = Path.of("build", "test");
        Files.createDirectories(workingDirectory);
        Path errorFile = Files.createTempFile(workingDirectory, "ERROR-", ".csv");
        try {
            IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
            String expectedZipErrorFileName = "ERROR-fileName.zip";

            Mockito.doThrow(new IOException("Error")).when(fileArchiverServiceMock)
                    .compressAndArchive(List.of(errorFile), Path.of("build/test/" + expectedZipErrorFileName), Path.of(sharedDirectory, ingestionFlowFileDTO.getOrganizationId() + "", ingestionFlowFileDTO.getFilePathName(), errorFolder));

            // When
            String result = service.archiveErrorFiles(workingDirectory, ingestionFlowFileDTO);

            // Then
            Assertions.assertNull(result);

        } finally {
            Files.delete(errorFile);
        }
    }
}
