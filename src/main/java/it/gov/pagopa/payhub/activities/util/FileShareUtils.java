package it.gov.pagopa.payhub.activities.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileShareUtils {

  private FileShareUtils() {
  }

  /**
   * This method expects two paths whose concatenation does not resolve into an outer folder. The
   * normalized path still starts with the first path.
   */
  public static Path concatenatePaths(String firstPath, String secondPath) {
    Path concatenatedPath = Paths.get(firstPath, secondPath).normalize();
    if (!concatenatedPath.startsWith(firstPath)) {
      log.debug("Invalid file path");
      throw new IllegalArgumentException("Invalid file path");
    }
    return concatenatedPath;
  }

  public static Path buildOrganizationBasePath(Path sharedFolder, Long organizationId) {
    return concatenatePaths(sharedFolder.toString(), String.valueOf(organizationId));
  }
}
