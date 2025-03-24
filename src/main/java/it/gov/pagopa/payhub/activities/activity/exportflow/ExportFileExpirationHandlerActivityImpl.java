package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.service.exportflow.ExportFileExpirationHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class ExportFileExpirationHandlerActivityImpl implements
    ExportFileExpirationHandlerActivity {

  private final ExportFileExpirationHandlerService exportFileExpirationHandlerService;

  public ExportFileExpirationHandlerActivityImpl(ExportFileExpirationHandlerService exportFileExpirationHandlerService) {
    this.exportFileExpirationHandlerService = exportFileExpirationHandlerService;
  }

  @Override
  public void handleExpiration(Long id, String codError) {
    log.info("Handling expiration of Export File having id {}", id);
    exportFileExpirationHandlerService.handleExpiration(id, codError);
  }
}
