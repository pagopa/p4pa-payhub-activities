package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;

import java.util.zip.ZipEntry;

/**
 * Utility class for securely handling file names within ZIP files.
 * <p>
 * This class provides methods to validate ZIP entry names to prevent
 * potential vulnerabilities, such as ZIP Slip attacks, by ensuring
 * that file names adhere to strict safety criteria.
 * </p>
 * <p>
 * This class is not instantiable.
 * </p>
 */
public class SecureFileUtils {
  private SecureFileUtils() {
  }

  /**
   * Validates the safety of a file name within a ZIP archive.
   *
   * The file name is considered safe if it:
   * <ul>
   *   <li>Starts with an alphanumeric character.</li>
   *   <li>Does not contain the string ".." (prevents directory traversal).</li>
   * </ul>
   *
   * @param fileName the name of the ZIP entry to validate.
   * @return the file name if it is deemed safe.
   * @throws IllegalArgumentException if the file name is deemed unsafe.
   */
  public static String checkFileName(String fileName) throws IllegalArgumentException {
    if (!Character.isLetterOrDigit(fileName.charAt(0)) || fileName.contains("..")) {
      throw new InvalidIngestionFileException("Potential Zip Slip exploit detected: " + fileName);
    }
    return fileName;
  }

  /**
   * Validates the safety of a {@link ZipEntry}'s file name within a ZIP archive.
   * <p>
   * This method extracts the file name from the {@code ZipEntry} and delegates
   * the validation to {@link #checkFileName(String)}.
   * </p>
   *
   * @param entry the ZIP entry to validate.
   * @return the original {@code ZipEntry} if the file name is deemed safe.
   * @throws IllegalArgumentException if the file name of the ZIP entry is deemed unsafe.
   */
  public static ZipEntry checkFileName(ZipEntry entry) throws IllegalArgumentException {
    checkFileName(entry.getName());
    return entry;
  }
}
