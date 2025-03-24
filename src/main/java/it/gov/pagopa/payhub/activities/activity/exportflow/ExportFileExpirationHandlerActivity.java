package it.gov.pagopa.payhub.activities.activity.exportflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.io.IOException;

/**
 * Interface for the ExportFileExpirationHandlerActivity.
 * Handles the expiration of an ExportFile identified by the provided ID.
 */
@ActivityInterface
public interface ExportFileExpirationHandlerActivity {

  @ActivityMethod
  void handleExpiration(Long id, String codError) throws IOException;
}
