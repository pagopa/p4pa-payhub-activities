package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
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
    private IngestionFlowFileArchiverService ingestionFlowFileArchiverServiceMock;
    @Mock
    private CsvService csvServiceMock;

    private TreasuryErrorsArchiverService treasuryErrorsArchiverService;

    private final String sharedDirectory = "/tmp";

    @BeforeEach
    void setUp() {
        treasuryErrorsArchiverService = new TreasuryErrorsArchiverService(sharedDirectory, errorFolder, ingestionFlowFileArchiverServiceMock, csvServiceMock);
    }

    @Test
    void testWriteErrors_whenValidInput_thenCreatesAndArchivesCsv() throws IOException {
        // Given
        List<TreasuryErrorDTO> errorDTOList = List.of(
                new TreasuryErrorDTO("file1", "2023", "B123", "ERR01", "Invalid data"),
                new TreasuryErrorDTO("file2", "2023", "B124", "ERR02", "Missing field")
        );
        Path workingDirectory = Path.of("build", "test");
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        Path expectedErrorFilePath = workingDirectory.resolve("ERROR-fileName.csv");

        // When
        treasuryErrorsArchiverService.writeErrors(workingDirectory, ingestionFlowFileDTO, errorDTOList);

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
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        Path expectedErrorFilePath = workingDirectory.resolve("ERROR-fileName.csv");

            Mockito.doThrow(new IOException("Error creating CSV"))
                    .when(csvServiceMock)
                    .createCsv(eq(expectedErrorFilePath), any(), any());

            // When & Then
            ActivitiesException exception = assertThrows(ActivitiesException.class, () ->
                    treasuryErrorsArchiverService.writeErrors(workingDirectory, ingestionFlowFileDTO, errorDTOList));
            assertEquals("Error creating CSV", exception.getMessage());
    }

    @Test
    void givenNoErrorsWhenArchiveErrorFilesThenReturnNull() throws IOException {
        // Given
        Path workingDirectory = Path.of("build");

        // When
        String result = treasuryErrorsArchiverService.archiveErrorFiles(workingDirectory, new IngestionFlowFileDTO());

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
            IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
            String expectedZipErrorFileName = "ERROR-fileName.zip";

            // When
            String result = treasuryErrorsArchiverService.archiveErrorFiles(workingDirectory, ingestionFlowFileDTO);

            // Then
            Assertions.assertEquals(expectedZipErrorFileName, result);

            Mockito.verify(ingestionFlowFileArchiverServiceMock)
                    .compressAndArchive(List.of(errorFile), Path.of(expectedZipErrorFileName), Path.of(sharedDirectory, ingestionFlowFileDTO.getFilePathName(), errorFolder));
        } finally {
            Files.delete(errorFile);
        }
    }

}