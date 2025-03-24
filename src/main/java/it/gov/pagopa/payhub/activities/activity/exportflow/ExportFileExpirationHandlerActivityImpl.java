package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.exportflow.ExportFileRetrieverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import java.io.IOException;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class ExportFileExpirationHandlerActivityImpl implements ExportFileExpirationHandlerActivity {
  private final ExportFileService exportFileService;
  private final ExportFileRetrieverService exportFileRetrieverService;

  public ExportFileExpirationHandlerActivityImpl(ExportFileService exportFileService,
      ExportFileRetrieverService exportFileRetrieverService) {
    this.exportFileService = exportFileService;
    this.exportFileRetrieverService = exportFileRetrieverService;
  }

  @Override
  public void handleExpiration(Long id, String codError) {
    log.info("Handling expiration of Export File having id {}", id);
    ExportFile file = exportFileService.findById(id)
        .orElseThrow(() -> new ExportFileNotFoundException(
            "Export file having id %s not found.".formatted(id)));

    try {
      if (!Files.deleteIfExists(exportFileRetrieverService.getFilePath(file))) {
        log.info("Export File having id {} does not exist", file.getExportFileId());
      }

      if (file.getStatus() != ExportFileStatus.EXPIRED) {
        if (exportFileService.updateStatus(file.getExportFileId(), file.getStatus(),
            ExportFileStatus.EXPIRED, codError) != 1) {
          throw new ExportFileNotFoundException(
              "Cannot update exportFile having id " + id + " to status "
                  + ExportFileStatus.EXPIRED);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(
          "Export File %s could not be deleted".formatted(file.getFileName()), e);
    }
  }
}
