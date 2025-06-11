package it.gov.pagopa.payhub.activities.activity.exportflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Handles the expiration of an ExportFile identified by the provided ID.
 */
@ActivityInterface
public interface ExportFileExpirationHandlerActivity {

  @ActivityMethod
  void handleExportExpiration(Long exportFileId);
}
