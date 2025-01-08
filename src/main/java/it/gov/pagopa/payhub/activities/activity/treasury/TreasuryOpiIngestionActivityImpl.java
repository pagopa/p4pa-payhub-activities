package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.*;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {
    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
    private final TreasuryOpiParserService treasuryOpiParserService;




    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileDao ingestionFlowFileDao,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryOpiParserService treasuryOpiParserService) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryOpiParserService = treasuryOpiParserService;
    }


    @Override
    public TreasuryIufResult processFile(Long ingestionFlowFileId) {
        log.info("Processing OPI treasury IngestionFlowFile {}", ingestionFlowFileId);

        try {
            IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

            List<Path> ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);


           List<TreasuryIufResult>  treasuryIufResultList =  ingestionFlowFiles.stream()
                    .map(path ->treasuryOpiParserService.parseData(path, ingestionFlowFileDTO, ingestionFlowFiles.size()))
                   .toList();

           return new TreasuryIufResult(
                   treasuryIufResultList.stream()
                           .flatMap(result -> result.getIufs().stream())
                           .distinct()
                           .toList(),
                   treasuryIufResultList.stream()
                           .allMatch(TreasuryIufResult::isSuccess)
           );

        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            return new TreasuryIufResult(Collections.emptyList(), false);
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
            Path originalFilePath = Paths.get(ingestionFlowFileDTO.getFilePath(), ingestionFlowFileDTO.getFileName());
            Path targetDirectory = Paths.get(ingestionFlowFileDTO.getFilePath(), archiveDirectory);
            ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
        }

        /**
         * Archives an error file to a specified target directory.
         * This method takes an error file and moves it to a target directory for archiving. It constructs
         * the original file path and the target directory path, then invokes the {@link IngestionFlowFileArchiverService}
         * to perform the archiving operation.
         *
         * @param errorFile the error file to be archived. This file is moved from its original location to the target directory.
         * @param targetDir the directory where the error file should be archived. The target directory path is constructed relative
         *                  to the parent directory of the error file.
         * @throws IOException if an I/O error occurs while archiving the file, such as issues with reading, writing, or accessing file paths.
         */
        private void archiveErrorFile(File errorFile, String targetDir) throws IOException {
            Path originalFilePath = Paths.get(errorFile.getParent(), errorFile.getName());
            Path targetDirectory = Paths.get(errorFile.getParent(), targetDir);
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
