package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.payhub.activities.util.NoticeFileUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Lazy
@Slf4j
@Service
public class DeleteMassiveNoticesFileActivityImpl implements DeleteMassiveNoticesFileActivity {

    private final IngestionFlowFileService ingestionFlowFileService;
    private final FoldersPathsConfig foldersPathsConfig;

    public DeleteMassiveNoticesFileActivityImpl(IngestionFlowFileService ingestionFlowFileService, FoldersPathsConfig foldersPathsConfig) {
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.foldersPathsConfig = foldersPathsConfig;
    }

    @Override
    public void deleteMassiveNoticesFile(Long ingestionFlowFileId) {

        IngestionFlowFile ingestionFlowFile = ingestionFlowFileService
                .findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("[FILE_NOT_FOUND] IngestionFlowFile with id " + ingestionFlowFileId + " not found"));

        Path noticePath = buildNoticePath(ingestionFlowFile);

        try {
            if (!Files.exists(noticePath)) {
                log.info("Notice retention file not found for ingestionFlowFileId={} at path={}", ingestionFlowFileId, noticePath);
                return;
            }
            Files.delete(noticePath);
            log.info("Notice retention file deleted for ingestionFlowFileId={} at path={}", ingestionFlowFileId, noticePath);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete notice retention file for ingestionFlowFileId=" + ingestionFlowFileId, e);
        }
    }

    private Path buildNoticePath(IngestionFlowFile ingestionFlowFile) {
        return FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), ingestionFlowFile.getOrganizationId())
                .resolve(ingestionFlowFile.getFilePathName())
                .resolve(foldersPathsConfig.getProcessTargetSubFolders().getArchive())
                .resolve(NoticeFileUtils.buildNoticeFileName(ingestionFlowFile) + AESUtils.CIPHER_EXTENSION);
    }
}
