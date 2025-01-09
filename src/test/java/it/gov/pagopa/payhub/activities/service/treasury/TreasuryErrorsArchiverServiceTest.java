package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.util.CsvUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryErrorsArchiverServiceTest {

    @Mock
    private IngestionFlowFileArchiverService ingestionFlowFileArchiverService;

    @InjectMocks
    private TreasuryErrorsArchiverService treasuryErrorsArchiverService;

    private final String tempDirectory = "/tmp/";
    private final String errorDirectory = "/error/";

    @BeforeEach
    void setUp() {
        treasuryErrorsArchiverService = new TreasuryErrorsArchiverService(ingestionFlowFileArchiverService, tempDirectory, errorDirectory);
    }

    @Test
    void testWriteErrors_whenValidInput_thenCreatesAndArchivesCsv() throws IOException {
        // Given
        List<TreasuryErrorDTO> errorDTOList = List.of(
                new TreasuryErrorDTO("file1", "2023", "B123", "ERR01", "Invalid data"),
                new TreasuryErrorDTO("file2", "2023", "B124", "ERR02", "Missing field")
        );
        String fileName = "testFile.xml";
        String expectedErrorFilePath = tempDirectory + "ERROR-" + fileName;

        // Mock CsvUtils.createCsv
        try (var csvUtilsMock = Mockito.mockStatic(CsvUtils.class)) {
            csvUtilsMock.when(() -> CsvUtils.createCsv(eq(expectedErrorFilePath), any(), any())).then(invocation -> null);

            // Mock archiving
            doNothing().when(ingestionFlowFileArchiverService).archive(anyList(), any(Path.class));

            // When
            treasuryErrorsArchiverService.writeErrors(errorDTOList, fileName);

            // Then
            csvUtilsMock.verify(() -> CsvUtils.createCsv(eq(expectedErrorFilePath), any(), any()), times(1));
            verify(ingestionFlowFileArchiverService, times(1)).archive(
                    List.of(Path.of(expectedErrorFilePath)),
                    Path.of(tempDirectory,errorDirectory)
            );
        }
    }

    @Test
    void testWriteErrors_whenIOException_thenThrowsActivitiesException() {
        // Given
        List<TreasuryErrorDTO> errorDTOList = List.of(
                new TreasuryErrorDTO("file1", "2023", "B123", "ERR01", "Invalid data")
        );
        String fileName = "testFile.xml";
        String expectedErrorFilePath = tempDirectory + "ERROR-" + fileName;

        // Mock CsvUtils.createCsv to throw IOException
        try (var csvUtilsMock = Mockito.mockStatic(CsvUtils.class)) {
            csvUtilsMock.when(() -> CsvUtils.createCsv(eq(expectedErrorFilePath), any(), any()))
                    .thenThrow(new IOException("Error creating CSV"));

            // When & Then
            ActivitiesException exception = assertThrows(ActivitiesException.class, () ->
                    treasuryErrorsArchiverService.writeErrors(errorDTOList, fileName));
            assertEquals("Error creating CSV", exception.getMessage());
        }
    }

    @Test
    void testArchiveErrorFile_whenValidInput_thenArchivesFile() throws IOException {
        // Given
        File errorFile = new File(tempDirectory + "ERROR-testFile.xml");
        String targetDir = "error/";

        // Mock archiving
        doNothing().when(ingestionFlowFileArchiverService).archive(anyList(), any(Path.class));

        // When
        treasuryErrorsArchiverService.archiveErrorFile(errorFile, targetDir);

        // Then
        verify(ingestionFlowFileArchiverService, times(1)).archive(
                List.of(Path.of(errorFile.getPath())),
                Path.of(tempDirectory, targetDir)
        );
    }

    @Test
    void testArchiveErrorFile_whenIOException_thenThrowsActivitiesException() throws IOException {
        // Given
        File errorFile = new File(tempDirectory + "ERROR-testFile.xml");
        String targetDir = "error/";

        // Mock archiving to throw IOException
        doThrow(new IOException("Error archiving file")).when(ingestionFlowFileArchiverService)
                .archive(anyList(), any(Path.class));

        // When & Then
        IOException exception = assertThrows(IOException.class, () ->
                treasuryErrorsArchiverService.archiveErrorFile(errorFile, targetDir));
        assertEquals("Error archiving file", exception.getMessage());
    }
}