package it.gov.pagopa.payhub.activities.service.exportflow;

import it.gov.pagopa.payhub.activities.dto.export.ErrorExportDTO;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.FileArchiverService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public abstract class ExportErrorArchiverService<T extends ErrorExportDTO> {

    private static final String ERRORFILE_PREFIX = "ERROR-";
    private final Path sharedDirectoryPath;
    private final String errorFolder;
    private final FileArchiverService fileArchiverService;
    private final CsvService csvService;

    protected ExportErrorArchiverService(Path sharedDirectoryPath, String errorFolder, FileArchiverService fileArchiverService, CsvService csvService) {
        this.sharedDirectoryPath = sharedDirectoryPath;
        this.errorFolder = errorFolder;
        this.fileArchiverService = fileArchiverService;
        this.csvService = csvService;
    }


    protected abstract List<String[]> getHeaders();

    /**
     * Writes a list of errors to a CSV file in the specified working directory.
     * The CSV file is named using the provided file name with the ".csv" extension.
     *
     * @param workingDirectory The working directory where the error CSV file will be created.
     * @param fileName         The base file name to use for the error CSV file.
     * @param errorList        The list of error DTOs to write to the CSV file.
     */
    public void writeErrors(Path workingDirectory, String fileName, List<T> errorList) {

        if(CollectionUtils.isEmpty(errorList)){
            return;
        }

        List<String[]> data = errorList.stream()
                .map(ErrorExportDTO::toCsvRow)
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
        } catch (IOException e){
            log.error("Something gone wrong while trying to archive error file!", e);
            return null;
        }
    }
}
