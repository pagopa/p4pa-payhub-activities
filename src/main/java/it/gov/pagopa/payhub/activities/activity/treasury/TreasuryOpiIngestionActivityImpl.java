package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.*;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Implementation of {@link TreasuryOpiIngestionActivity} for processing OPI treasury ingestion files.
 * This class handles file retrieval, parsing, archiving, and deletion of OPI treasury files.
 */
@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {
    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
    private final TreasuryOpiParserService treasuryOpiParserService;
    private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;
    private final String archiveDirectory;
    private final String errorDirectory;

    /**
     * Constructor to initialize dependencies for OPI treasury ingestion.
     *
     * @param ingestionFlowFileDao DAO for accessing ingestion flow file records.
     * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
     * @param treasuryOpiParserService Service for parsing treasury OPI files.
     * @param ingestionFlowFileArchiverService Service for archiving files.
     * @param archiveDirectory Directory for archiving processed files.
     * @param errorDirectory Directory for handling error files.
     */
    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileDao ingestionFlowFileDao,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryOpiParserService treasuryOpiParserService,
            IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
            String archiveDirectory,
            String errorDirectory) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryOpiParserService = treasuryOpiParserService;
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
        this.archiveDirectory = archiveDirectory;
        this.errorDirectory = errorDirectory;
    }

    /**
     * Processes a given ingestion flow file by ID.
     * The method retrieves, parses, and archives the specified file while handling any exceptions during processing.
     *
     * @param ingestionFlowFileId ID of the ingestion flow file to process.
     * @return {@link TreasuryIufResult} containing the results of the processing.
     */
    @Override
    public TreasuryIufResult processFile(Long ingestionFlowFileId) {
        log.info("Processing OPI treasury IngestionFlowFile {}", ingestionFlowFileId);
        IngestionFlowFileDTO ingestionFlowFileDTO = null;
        try {
            ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

            List<Path> ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);

            IngestionFlowFileDTO finalIngestionFlowFileDTO = ingestionFlowFileDTO;
            List<TreasuryIufResult> treasuryIufResultList = ingestionFlowFiles.stream()
                    .map(path -> {
                        try {
                            TreasuryIufResult result = treasuryOpiParserService.parseData(path, finalIngestionFlowFileDTO, ingestionFlowFiles.size(), errorDirectory);
                            archive(finalIngestionFlowFileDTO);
                            return result;
                        } catch (Exception e) {
                            log.error("Error processing file {}: {}", path, e.getMessage());
                            return new TreasuryIufResult(Collections.emptyList(), false, e.getMessage());
                        }
                    })
                    .toList();

            return new TreasuryIufResult(
                    treasuryIufResultList.stream()
                            .flatMap(result -> result.getIufs().stream())
                            .distinct()
                            .toList(),
                    treasuryIufResultList.stream().allMatch(TreasuryIufResult::isSuccess),
                    treasuryIufResultList.stream().allMatch(TreasuryIufResult::isSuccess)?null:"error occured"
            );
        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            deletion(new File(ingestionFlowFileDTO.getFilePathName()));
            return new TreasuryIufResult(Collections.emptyList(), false, e.getMessage());
        }
    }

    /**
     * Finds an ingestion flow file record by its ID.
     *
     * @param ingestionFlowFileId ID of the ingestion flow file to find.
     * @return {@link IngestionFlowFileDTO} containing details of the ingestion flow file.
     * @throws IngestionFlowFileNotFoundException if the file is not found.
     */
    private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
        IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
        if (!ingestionFlowFileDTO.getFlowFileType().equals(IngestionFlowFileType.OPI)) {
            throw new IllegalArgumentException("Invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
        }
        return ingestionFlowFileDTO;
    }

    /**
     * Retrieves and unzips files associated with the given ingestion flow file DTO.
     *
     * @param ingestionFlowFileDTO DTO containing details of the ingestion flow file.
     * @return A list of {@link Path} objects representing the retrieved files.
     * @throws IOException if an error occurs during retrieval or unzipping.
     */
    private List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
        return ingestionFlowFileRetrieverService
                .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
    }

    /**
     * Archives the file specified in the given {@link IngestionFlowFileDTO}.
     * The file is moved to the archive directory located within the same file path.
     *
     * @param ingestionFlowFileDTO DTO containing details of the file to be archived.
     * @throws IOException if an error occurs during file movement or directory creation.
     */
    private void archive(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
        Path originalFilePath = Paths.get(ingestionFlowFileDTO.getFilePathName(), ingestionFlowFileDTO.getFileName());
        Path targetDirectory = Paths.get(ingestionFlowFileDTO.getFilePathName(), archiveDirectory);
        ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
    }

    /**
     * Deletes the specified file if it is not null.
     *
     * @param file2Delete The file to delete.
     */
    private void deletion(File file2Delete) {
        if (file2Delete != null) {
            try {
                Files.delete(file2Delete.toPath());
            } catch (IOException e) {
                throw new ActivitiesException("Error occured while delete file: " + file2Delete);
            }
        }
    }
}
