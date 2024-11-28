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
 * Utility class for file operations, including ZIP file validation and extraction.
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
	 *
	 * @param source the path to the ZIP file.
	 * @param target the target directory for extraction.
	 * @throws InvalidIngestionFileException if the ZIP file contains invalid entries or an error occurs during extraction.
	 */
	public static void unzip(Path source, Path target) {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
			ZipEntry zipEntry = zis.getNextEntry();
			SecureFileUtils.checkFileName(zipEntry);
			while (zipEntry != null) {
				if (zipEntry.isDirectory()) {
					throw new InvalidIngestionFileException("ZIP file contains directories, but only files are expected");
				}
				Path targetPath = zipSlipProtect(zipEntry, target);
				Files.createDirectories(targetPath);
				Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error while unzipping file: " + source);
		}
	}

	/**
	 * Protects against ZIP Slip vulnerability by validating the path of an extracted entry.
	 *
	 * @param zipEntry the ZIP entry to validate.
	 * @param targetDir the target directory for extraction.
	 * @return a safe, normalized path within the target directory.
	 * @throws InvalidIngestionFileException if the ZIP entry is outside the target directory.
	 */
	public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) {
		String checkedFilename = SecureFileUtils.checkFileName(zipEntry.getName());
		Path targetDirResolved = targetDir.resolve(checkedFilename);
		Path normalizePath = targetDirResolved.normalize();
		if (!normalizePath.startsWith(targetDir)) {
			throw new InvalidIngestionFileException("Bad zip entry: " + zipEntry.getName());
		}
		return normalizePath;
	}
}
