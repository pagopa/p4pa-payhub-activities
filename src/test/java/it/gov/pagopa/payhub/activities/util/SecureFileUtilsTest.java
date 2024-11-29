package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import org.junit.jupiter.api.Test;

import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

class SecureFileUtilsTest {

  @Test
  void givenValidFileNameThenOk() throws IllegalArgumentException {
    String validFileName = "safeFile.txt";
    assertDoesNotThrow(() -> SecureFileUtils.checkFileName(validFileName));
  }

  @Test
  void givenInvalidFileNameStartingWithNonAlphanumericThenException() {
    String invalidFileName = "/unsafeFile.txt";
    assertThrows(InvalidIngestionFileException.class, () -> SecureFileUtils.checkFileName(invalidFileName));
  }

  @Test
  void givenInvalidFileNameContainingDotDotThenException() {
    String invalidFileName = "safe/../../unsafeFile.txt";
    assertThrows(InvalidIngestionFileException.class, () -> SecureFileUtils.checkFileName(invalidFileName));
  }

  @Test
  void givenValidZipEntryThenReturnZipEntry() throws IllegalArgumentException {
    ZipEntry validEntry = new ZipEntry("safeFile.txt");
    assertEquals(validEntry, SecureFileUtils.checkFileName(validEntry));
  }
}