package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpiParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

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

    /**
     * Constructor to initialize dependencies for OPI treasury ingestion.
     *
     * @param ingestionFlowFileDao DAO for accessing ingestion flow file records.
     * @param ingestionFlowFileRetrieverService Service for retrieving and unzipping ingestion flow files.
     * @param treasuryOpiParserService Service for parsing treasury OPI files.
     * @param ingestionFlowFileArchiverService Service for archiving files.
     */
    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileDao ingestionFlowFileDao,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryOpiParserService treasuryOpiParserService,
            IngestionFlowFileArchiverService ingestionFlowFileArchiverService

    ) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryOpiParserService = treasuryOpiParserService;
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
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
        List<Path> ingestionFlowFilesRetrieved = null;
        try {
            IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

            ingestionFlowFilesRetrieved = retrieveFiles(ingestionFlowFileDTO);
            int ingestionFlowFilesRetrievedSize = ingestionFlowFilesRetrieved.size();

            List<TreasuryIufResult> treasuryIufResultList = ingestionFlowFilesRetrieved.stream()
                    .map(path -> {
                        try {
                            return treasuryOpiParserService.parseData(path, ingestionFlowFileDTO, ingestionFlowFilesRetrievedSize);
                        } catch (Exception e) {
                            log.error("Error processing file {}: {}", path, e.getMessage());
                            return new TreasuryIufResult(Collections.emptyList(), false, e.getMessage());
                        }
                    })
                    .toList();

            ingestionFlowFileArchiverService.archive(ingestionFlowFileDTO);
            boolean isSuccess= treasuryIufResultList.stream().allMatch(TreasuryIufResult::isSuccess);

            return new TreasuryIufResult(
                    treasuryIufResultList.stream()
                            .flatMap(result -> result.getIufs().stream())
                            .distinct()
                            .toList(),
                    isSuccess,
                    isSuccess?null:"error occurred"
            );
        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            return new TreasuryIufResult(Collections.emptyList(), false, e.getMessage());
        } finally {
            deletion(ingestionFlowFilesRetrieved);
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

        if (!IngestionFlowFileType.OPI.equals(ingestionFlowFileDTO.getFlowFileType())) {
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
     * Deletes the specified List of path if it is not null.
     *
     * @param pathsToDelete The list of path to delete.
     */
    private void deletion(List<Path> pathsToDelete) {
        if (pathsToDelete != null && !pathsToDelete.isEmpty()) {
            for (Path pathToDelete : pathsToDelete) {
                try {
                    Files.delete(pathToDelete);
                } catch (IOException e) {
                    throw new ActivitiesException("Error occurred while deleting file: " + pathToDelete + " " +e.getMessage());
                }
            }
        }
    }

}
