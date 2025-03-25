package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileErrorDTO;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.util.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * A base service for archiving error files.
 * This class provides common functionality for writing error data to CSV files and archiving them into ZIP archives.
 * It is designed to be extended by specific error archiver services for different types of error DTOs.
 *
 * @param <T> The type of the error DTO.
 */
@Slf4j
public abstract class ErrorArchiverService<T extends IngestionFlowFileErrorDTO> {

    private static final String ERRORFILE_PREFIX = "ERROR-";

    private final Path sharedDirectoryPath;
    private final String errorFolder;
    private final FileArchiverService fileArchiverService;
    private final CsvService csvService;

    protected ErrorArchiverService(String sharedDirectoryPath, String errorFolder, FileArchiverService fileArchiverService, CsvService csvService) {
        this.sharedDirectoryPath = Path.of(sharedDirectoryPath);
        this.errorFolder = errorFolder;
        this.fileArchiverService = fileArchiverService;
        this.csvService = csvService;
    }


    /**
     * Retrieves the headers for the error CSV file.
     * This method must be implemented by subclasses to provide the specific headers for the CSV file.
     *
     * @return A list of strings representing the CSV headers.
     */
    protected abstract List<String[]> getHeaders();

    /**
     * Converts an error DTO to a CSV row.
     * This method must be implemented by subclasses to provide the specific logic for converting an error DTO to a CSV row.
     *
     * @param error The error DTO to convert.
     * @return An array of strings representing the CSV row.
     */
    protected abstract String[] toCsvRow(T error);

    /**
     * Writes a list of errors to a CSV file in the specified working directory.
     * The CSV file is named using the provided file name with the ".csv" extension.
     *
     * @param workingDirectory The working directory where the error CSV file will be created.
     * @param fileName         The base file name to use for the error CSV file.
     * @param errorList        The list of error DTOs to write to the CSV file.
     */
    public void writeErrors(Path workingDirectory, String fileName, List<T> errorList) {
        if (CollectionUtils.isEmpty(errorList)) {
            return;
        }

        List<String[]> data = errorList.stream()
                .map(this::toCsvRow)
                .toList();

        try {
            String errorFileName = ERRORFILE_PREFIX + Utilities.replaceFileExtension(fileName, ".csv");
            Path errorCsvFilePath = workingDirectory.resolve(errorFileName);

            csvService.createCsv(errorCsvFilePath, getHeaders(), data);
            log.info("Error CSV created: {}", errorCsvFilePath);

        } catch (IOException e) {
            throw new NotRetryableActivityException(e.getMessage());
        }
    }

    /**
     * Archives error files in the specified working directory into a ZIP archive and moves it to a target directory.
     * The ZIP archive is named using the provided file name with the ".zip" extension.
     *
     * @param workingDirectory The working directory where error files are located.
     * @param organizationId   The ID of the organization, used to construct the target directory.
     * @param filePathName     The path name of the file, used to construct the target directory.
     * @param fileName         The base file name to use for the ZIP archive.
     * @return The name of the ZIP archive if archiving is successful, or null if no errors are found or an error occurs during archiving.
     */
    public String archiveErrorFiles(Path workingDirectory, Long organizationId, String filePathName, String fileName) {
        try {
            List<Path> errorFiles;
            try (Stream<Path> fileListStream = Files.list(workingDirectory)) {
                errorFiles = fileListStream
                        .filter(f -> f.getFileName().toString().startsWith(ERRORFILE_PREFIX))
                        .toList();
            }

            if (!errorFiles.isEmpty()) {

                Path targetDirectory = sharedDirectoryPath
                        .resolve(String.valueOf(organizationId))
                        .resolve(filePathName)
                        .resolve(errorFolder);

                String zipFileName = ERRORFILE_PREFIX + Utilities.replaceFileExtension(fileName, ".zip");
                Path zipFile = workingDirectory.resolve(zipFileName);

                fileArchiverService.compressAndArchive(errorFiles, zipFile, targetDirectory);

                return zipFileName;
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Something gone wrong while trying to archive error file!", e);
            return null;
        }
    }
}
