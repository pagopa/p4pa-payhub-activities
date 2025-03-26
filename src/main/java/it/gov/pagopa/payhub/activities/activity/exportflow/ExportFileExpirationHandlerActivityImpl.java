package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class ExportFileExpirationHandlerActivityImpl implements
    ExportFileExpirationHandlerActivity {

  /**
   * the shared folder
   */
  private final Path sharedDirectoryPath;

  private final ExportFileService exportFileService;

  public ExportFileExpirationHandlerActivityImpl(
      @Value("${folders.shared}") String sharedFolder, ExportFileService exportFileService) {
    this.sharedDirectoryPath = Path.of(sharedFolder);
    this.exportFileService = exportFileService;

    if (!Files.exists(sharedDirectoryPath)) {
      throw new IllegalStateException("Shared folder doesn't exist: " + sharedDirectoryPath);
    }
  }

  @Override
  public void handleExpiration(Long exportFileId) {
    log.info("Handling expiration of Export File having exportFileId {}", exportFileId);
    ExportFile file = exportFileService.findById(exportFileId)
        .orElseThrow(() -> new ExportFileNotFoundException(
            "Export file having exportFileId %s not found.".formatted(exportFileId)));

    Path exportFilePath = getFilePath(file);
    if (exportFilePath != null) {
      try {
        if (!Files.deleteIfExists(exportFilePath)) {
          log.info("Export File having exportFileId {} does not exist", file.getExportFileId());
        }
      } catch (IOException e) {
        throw new IllegalStateException(
            "Export File %s could not be deleted".formatted(file.getFileName()), e);
      }
    } else {
      log.info("Export File path name of file having id {}, status {} and type {} was null",
          file.getExportFileId(), file.getStatus(), file.getFlowFileType());
    }

    if (file.getStatus() != ExportFileStatus.EXPIRED &&
        exportFileService.updateStatus(file.getExportFileId(), file.getStatus(),
            ExportFileStatus.EXPIRED, null) != 1) {
      throw new ExportFileNotFoundException(
          "Cannot update exportFile having exportFileId " + file.getExportFileId()
              + " from status " + file.getStatus() + " to status "
              + ExportFileStatus.EXPIRED);
    }
  }


  private Path getFilePath(ExportFile exportFile) {
    if (StringUtils.isEmpty(exportFile.getFilePathName())) {
      return null;
    }

    Path organizationBasePath = FileShareUtils.buildOrganizationBasePath(sharedDirectoryPath,
        exportFile.getOrganizationId());

    return organizationBasePath
        .resolve(exportFile.getFilePathName())
        .resolve(exportFile.getFileName() + AESUtils.CIPHER_EXTENSION);
  }
}
