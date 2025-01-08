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




    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileDao ingestionFlowFileDao,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryOpiParserService treasuryOpiParserService, IngestionFlowFileArchiverService ingestionFlowFileArchiverService, String archiveDirectory, String errorDirectory) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryOpiParserService = treasuryOpiParserService;
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
        this.archiveDirectory = archiveDirectory;
        this.errorDirectory = errorDirectory;
    }


    @Override
    public TreasuryIufResult processFile(Long ingestionFlowFileId) {
        log.info("Processing OPI treasury IngestionFlowFile {}", ingestionFlowFileId);
        IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);
        try {

            List<Path> ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);


           List<TreasuryIufResult>  treasuryIufResultList =  ingestionFlowFiles.stream()
                    .map(path -> {
                        try {
                            TreasuryIufResult result = treasuryOpiParserService.parseData(path, ingestionFlowFileDTO, ingestionFlowFiles.size(), errorDirectory);
                            archive(ingestionFlowFileDTO);
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
                   treasuryIufResultList.stream()
                           .allMatch(TreasuryIufResult::isSuccess),
                   null
           );

        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            deletion(new File(ingestionFlowFileDTO.getFilePathName()));
            return new TreasuryIufResult(Collections.emptyList(), false, e.getMessage());
        }
    }

    private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
        IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
        if (!ingestionFlowFileDTO.getFlowFileType().equals(IngestionFlowFileType.OPI)) {
            throw new IllegalArgumentException("invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
        }
        return ingestionFlowFileDTO;
    }

    private List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {

        return ingestionFlowFileRetrieverService
                .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
    }
/**
 * Archives the file specified in the given {@link IngestionFlowFileDTO}. The file is moved to
 * the archive directory located within the same file path.
 *
 * @param ingestionFlowFileDTO the DTO containing details of the file to be archived.
 * @throws IOException if an error occurs during file movement or directory creation.
 */
        private void archive(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
            Path originalFilePath = Paths.get(ingestionFlowFileDTO.getFilePathName(), ingestionFlowFileDTO.getFileName());
            Path targetDirectory = Paths.get(ingestionFlowFileDTO.getFilePathName(), archiveDirectory);
            ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
        }



        /**
         * Delete the specified file if not null.
         *
         * @param file2Delete the file to delete.
         * @throws IOException if an error occurs during deletion.
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
