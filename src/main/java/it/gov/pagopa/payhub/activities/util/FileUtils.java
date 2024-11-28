package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for performing common file operations, including
 * validation and extraction of ZIP files.
 * <p>
 * This class provides secure methods to handle files, ensuring safety
 * against potential vulnerabilities, such as ZIP Slip attacks.
 * </p>
 * <p>
 * The class is designed to be a utility and cannot be instantiated.
 * </p>
 */
public class FileUtils {

	private FileUtils() {
	}

	/**
	 * Validates that the file exists and is a regular file.
	 *
	 * @param filePath the path to the file.
	 * @throws InvalidIngestionFileException if the file does not exist or is not a regular file.
	 */
	public static void validateFile(Path filePath) {
		if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
			throw new InvalidIngestionFileException("File non trovato: " + filePath);
		}
	}

	/**
	 * Checks if the specified file is a valid archive by analyzing its signature.
	 *
	 * @param zipFilePath the path to the ZIP file to check.
	 * @return true if the file is a valid ZIP archive; false otherwise.
	 * @throws InvalidIngestionFileException if the file is not a valid archive.
	 */
	public static boolean isArchive(Path zipFilePath) {
		try (RandomAccessFile raf = new RandomAccessFile(zipFilePath.toFile(), "r")) {
			int fileSignature = raf.readInt();
			return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Invalid zip file");
		}
	}

	/**
	 * Extracts the contents of a ZIP file to a target directory.
	 * <p>
	 * This method ensures that the extracted files remain within the target directory
	 * to prevent security vulnerabilities such as ZIP Slip attacks. It also validates
	 * that the ZIP file does not contain directories or invalid entries.
	 * </p>
	 *
	 * @param source the path to the ZIP file to be extracted.
	 * @param target the target directory where the contents will be extracted.
	 * @throws InvalidIngestionFileException if the ZIP file contains directories, invalid file entries,
	 *                                       or if an error occurs during extraction.
	 */
	public static void unzip(Path source, Path target) {
		validateZipFile(source); // Validate before processing
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (zipEntry.isDirectory()) {
					throw new InvalidIngestionFileException("ZIP contains directories, only files are expected");
				}
				Path targetPath = zipSlipProtect(zipEntry, target);
				Files.createDirectories(targetPath.getParent()); // Ensure parent directory exists
				Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
				zipEntry = zis.getNextEntry();
			}
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error unzipping file: " + source.getFileName());
		}
	}

	/**
	 * Protects against ZIP Slip vulnerabilities by ensuring extracted file paths
	 * are confined within the target directory.
	 * <p>
	 * ZIP Slip vulnerabilities occur when malicious ZIP files contain entries with
	 * paths that attempt to traverse outside the intended extraction directory.
	 * This method validates the normalized path of the extracted entry and prevents
	 * such exploits.
	 * </p>
	 *
	 * @param zipEntry the ZIP entry to validate.
	 * @param targetDir the target directory where the file should be extracted.
	 * @return a secure, normalized path within the target directory.
	 * @throws InvalidIngestionFileException if the ZIP entry resolves to a path
	 *                                       outside of the target directory.
	 */
	public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) {
		String checkedFilename = SecureFileUtils.checkFileName(zipEntry.getName());
		Path targetDirResolved = targetDir.resolve(checkedFilename).normalize();
		if (!targetDirResolved.startsWith(targetDir)) {
			throw new InvalidIngestionFileException("ZIP entry resolves outside of target directory: " + zipEntry.getName());
		}
		return targetDirResolved;
	}

	/**
	 * Validates the contents of a ZIP file without extracting them.
	 * <p>
	 * This method inspects all entries in the ZIP file to ensure they do not
	 * contain directories or invalid paths that might indicate an attempted
	 * directory traversal attack. This validation is performed before any extraction
	 * occurs.
	 * </p>
	 *
	 * @param source the path to the ZIP file to validate.
	 * @throws InvalidIngestionFileException if the ZIP file contains directories,
	 *                                       invalid paths, or if an error occurs during validation.
	 */
	public static void validateZipFile(Path source) {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
			ZipEntry zipEntry;
			while ((zipEntry = zis.getNextEntry()) != null) {
				if (zipEntry.isDirectory() || zipEntry.getName().contains("..")) {
					throw new InvalidIngestionFileException("ZIP contains invalid entries: " + zipEntry.getName());
				}
			}
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error validating ZIP file: " + source);
		}
	}
}
