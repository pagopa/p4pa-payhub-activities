package it.gov.pagopa.payhub.activities.util;

import java.util.zip.ZipEntry;

public class SecureFileUtils {
  private SecureFileUtils() {
  }

  /**
   * Checks if the name of the zip entry is safe.
   * The name must:
   * - Start with an alphanumeric character.
   * - Not contain the string "..".
   * @param fileName The name of the zip entry.
   * @return fileName if the name is safe.
   */
  public static String checkFileName(String fileName) throws IllegalArgumentException {
    if (!Character.isLetterOrDigit(fileName.charAt(0))
        || fileName.contains("..")) {
      throw new IllegalArgumentException("Potential Zip Slip exploit detected: " + fileName);
    }
    return fileName;
  }

  public static ZipEntry checkFileName(ZipEntry entry) throws IllegalArgumentException {
    checkFileName(entry.getName());
    return entry;
  }
}
