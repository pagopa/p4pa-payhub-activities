package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class TreasuryErrorsArchiverServiceTest {

    private final String errorFolder = "error";
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private CsvService csvServiceMock;

    private TreasuryErrorsArchiverService service;

    private final String sharedDirectory = "/tmp";

    @BeforeEach
    void setUp() {
        service = new TreasuryErrorsArchiverService(sharedDirectory, errorFolder, fileArchiverServiceMock, csvServiceMock);
    }

    @Test
    void testWriteErrors_whenValidInput_thenCreatesAndArchivesCsv() throws IOException {
        // Given
        List<TreasuryErrorDTO> errorDTOList = List.of(
                new TreasuryErrorDTO("file1", "2023", "B123", "ERR01", "Invalid data"),
                new TreasuryErrorDTO("file2", "2023", "B124", "ERR02", "Missing field")
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
        // Given
        List<TreasuryErrorDTO> errorDTOList = List.of(
                new TreasuryErrorDTO("file1", "2023", "B123", "ERR01", "Invalid data")
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

            Mockito.verify(fileArchiverServiceMock)
                    .compressAndArchive(List.of(errorFile), Path.of("build/test/"+expectedZipErrorFileName), Path.of(sharedDirectory, ingestionFlowFileDTO.getOrganizationId()+"",ingestionFlowFileDTO.getFilePathName(), errorFolder));
        } finally {
            Files.delete(errorFile);
        }
    }

}