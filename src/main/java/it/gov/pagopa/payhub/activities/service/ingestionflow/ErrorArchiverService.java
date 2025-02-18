package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileErroDTO;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Slf4j
public abstract class ErrorArchiverService {

    private static final String ERRORFILE_PREFIX = "ERROR-";

    private final Path sharedDirectoryPath;
    private final String errorFolder;
    private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;
    private final CsvService csvService;

    protected ErrorArchiverService(
            String sharedFolder,
            String errorFolder,
            IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
            CsvService csvService
    ) {
        this.sharedDirectoryPath = Path.of(sharedFolder);
        this.errorFolder = errorFolder;
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
        this.csvService = csvService;
    }

    /**
     * Writes error data into a CSV file.
     *
     * @param workingDirectory  The directory where the file should be created.
     * @param ingestionFlowFile The metadata of the ingestion file.
     * @param errorList         The list of errors to write.
     * @param headers           The headers of the CSV file.
     */
    public <T extends IngestionFlowFileErroDTO> void writeErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFile,
                                                                 List<T> errorList, List<String> headers) {
        List<String[]> data = errorList.stream()
                .map(IngestionFlowFileErroDTO::toCsvRow)
                .toList();

        try {
            List<String[]> headerList = new ArrayList<>();
            headerList.add(headers.toArray(new String[0]));

            String errorFileName = ERRORFILE_PREFIX + Utilities.replaceFileExtension(ingestionFlowFile.getFileName(), ".csv");
            Path errorCsvFilePath = workingDirectory.resolve(errorFileName);

            csvService.createCsv(errorCsvFilePath, headerList, data);
            log.info("Error CSV created: {}", errorCsvFilePath);

        } catch (IOException e) {
            throw new NotRetryableActivityException(e.getMessage());
        }
    }

    /**
     * Archives error files into a ZIP archive.
     *
     * @param workingDirectory  The directory containing the error files.
     * @param ingestionFlowFile The ingestion metadata.
     * @return The name of the archived file, or null if no errors were found.
     */
    public String archiveErrorFiles(Path workingDirectory, IngestionFlowFile ingestionFlowFile) {
        try {
            List<Path> errorFiles;
            try (Stream<Path> fileListStream = Files.list(workingDirectory)) {
                errorFiles = fileListStream
                        .filter(f -> f.getFileName().toString().startsWith(ERRORFILE_PREFIX))
                        .toList();
            }

            if (!errorFiles.isEmpty()) {
                Path targetDirectory = sharedDirectoryPath
                        .resolve(String.valueOf(ingestionFlowFile.getOrganizationId()))
                        .resolve(ingestionFlowFile.getFilePathName())
                        .resolve(errorFolder);

                String zipFileName = ERRORFILE_PREFIX + Utilities.replaceFileExtension(ingestionFlowFile.getFileName(), ".zip");
                Path zipFile = Path.of(zipFileName);

                ingestionFlowFileArchiverService.compressAndArchive(errorFiles, zipFile, targetDirectory);
                log.info("Archived error file: {}", zipFile);

                return zipFileName;
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error archiving installment errors!", e);
            return null;
        }
    }
}
