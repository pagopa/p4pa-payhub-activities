package it.gov.pagopa.payhub.activities.service.exportflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
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
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling expiration of export files, including deletion from the
 * file share.
 */
@Lazy
@Slf4j
@Service
public class ExportFileExpirationHandlerService {

  /**
   * the shared folder
   */
  private final Path sharedDirectoryPath;

  private final ExportFileService exportFileService;

  public ExportFileExpirationHandlerService(
      @Value("${folders.shared}") String sharedFolder, ExportFileService exportFileService) {
    this.sharedDirectoryPath = Path.of(sharedFolder);
    this.exportFileService = exportFileService;

    if (!Files.exists(sharedDirectoryPath)) {
      throw new IllegalStateException("Shared folder doesn't exist: " + sharedDirectoryPath);
    }
  }

  public void handleExpiration(Long id, String codError) {
    ExportFile file = exportFileService.findById(id)
        .orElseThrow(() -> new ExportFileNotFoundException(
            "Export file having id %s not found.".formatted(id)));

    try {
      if (!Files.deleteIfExists(getFilePath(file))) {
        log.info("Export File having id {} does not exist", file.getExportFileId());
      }

      if (file.getStatus() != ExportFileStatus.EXPIRED &&
          exportFileService.updateStatus(file.getExportFileId(), file.getStatus(),
              ExportFileStatus.EXPIRED, codError) != 1) {
        throw new ExportFileNotFoundException(
            "Cannot update exportFile having id " + file.getExportFileId() + " to status "
                + ExportFileStatus.EXPIRED);
      }
    } catch (IOException e) {
      throw new IllegalStateException(
          "Export File %s could not be deleted".formatted(file.getFileName()), e);
    }
  }

  private Path getFilePath(ExportFile exportFile) {
    if (StringUtils.isEmpty(exportFile.getFilePathName())) {
      throw new ExportFileNotFoundException("ExportFile not ready");
    }
    Path organizationBasePath = FileShareUtils.buildOrganizationBasePath(sharedDirectoryPath,
        exportFile.getOrganizationId());

    return organizationBasePath
        .resolve(exportFile.getFilePathName());
  }
}
