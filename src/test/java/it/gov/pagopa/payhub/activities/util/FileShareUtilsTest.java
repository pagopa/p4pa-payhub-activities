package it.gov.pagopa.payhub.activities.util;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileShareUtilsTest {

  @Test
  void testBuildOrganizationPath() {
    Path sharedFolder = Path.of("/shared");
    Path result = FileShareUtils.buildOrganizationBasePath(sharedFolder, 1L);

    Assertions.assertEquals(Path.of("/shared/1"), result);
  }

}