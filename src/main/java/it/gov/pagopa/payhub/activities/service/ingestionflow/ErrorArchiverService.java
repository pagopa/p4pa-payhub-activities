package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileErrorDTO;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Slf4j
public abstract class ErrorArchiverService<T extends IngestionFlowFileErrorDTO> {

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
    public void writeErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFile,
                            List<T> errorList, List<String> headers) {
        List<String[]> data = errorList.stream()
                .map(IngestionFlowFileErrorDTO::toCsvRow)
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
     * Archives an error file to a specified target directory.
     * This method takes an error file and moves it to a target directory for archiving. It constructs
     * the original file path and the target directory path, then invokes the {@link IngestionFlowFileArchiverService}
     * to perform the archiving operation.
     *
     * @param workingDirectory     the working directory where to search for error files to be archived. This file is moved from its original location to the target directory.
     * @param ingestionFlowFileDTO the ingestion flow file
     */
    public String archiveErrorFiles(Path workingDirectory, IngestionFlowFile ingestionFlowFileDTO) {
        try {
            List<Path> errorFiles;
            try (Stream<Path> fileListStream = Files.list(workingDirectory)) {
                errorFiles = fileListStream
                        .filter(f -> f.getFileName().toString().startsWith(ERRORFILE_PREFIX))
                        .toList();
            }

            if (!errorFiles.isEmpty()) {

                Path targetDirectory = createTargetDirectory(ingestionFlowFileDTO);

                String zipFileName = ERRORFILE_PREFIX + Utilities.replaceFileExtension(ingestionFlowFileDTO.getFileName(), ".zip");
                Path zipFile = Path.of(workingDirectory+"/"+zipFileName);

                ingestionFlowFileArchiverService.compressAndArchive(errorFiles, zipFile, targetDirectory);

                return zipFileName;
            } else {
                return null;
            }
        } catch (IOException e){
            log.error("Something gone wrong while trying to archive error file!", e);
            return null;
        }
    }

    public Path createTargetDirectory(IngestionFlowFile ingestionFlowFileDTO) {
        return sharedDirectoryPath
                .resolve(String.valueOf(ingestionFlowFileDTO.getOrganizationId()))
                .resolve(ingestionFlowFileDTO.getFilePathName())
                .resolve(errorFolder);
    }

    protected abstract void writeErrors(Path workingDirectory, IngestionFlowFile ingestionFlowFileDTO, List<T> errorList);
}
