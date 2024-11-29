package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
	// Maximum number of entries to extract from the ZIP file
	private static final int MAX_ENTRIES = 1000;
	// Maximum total size (in bytes) of all uncompressed data
	private static final long MAX_UNCOMPRESSED_SIZE = 50 * 1024 * 1024L;  // 50MB
	// Maximum allowed compression ratio (compressed size / uncompressed size)
	private static final double MAX_COMPRESSION_RATIO = 0.1;  // 10%

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
	 * Extracts the contents of a ZIP file to a specified target directory.
	 * <p>
	 * This method ensures that the extracted files remain within the target directory
	 * to prevent security vulnerabilities, such as ZIP Slip attacks. It validates:
	 * <ul>
	 *   <li>The number of entries in the ZIP file does not exceed a predefined threshold.</li>
	 *   <li>The total uncompressed size of the extracted data does not exceed a maximum allowed size.</li>
	 *   <li>The compression ratio of each entry is within acceptable limits to mitigate ZIP bomb attacks.</li>
	 * </ul>
	 * The method also dynamically calculates the actual size of uncompressed entries while extracting them
	 * to ensure accurate and secure validation.
	 * </p>
	 *
	 * @param source the path to the ZIP file to be extracted.
	 * @param target the target directory where the contents will be extracted.
	 * @throws InvalidIngestionFileException if:
	 *                                        <ul>
	 *                                          <li>The ZIP file contains an entry with an invalid name
	 *                                          that could lead to a ZIP Slip attack.</li>
	 *                                          <li>The ZIP file exceeds the maximum allowed uncompressed size.</li>
	 *                                          <li>The ZIP file contains an excessive number of entries.</li>
	 *                                          <li>An entry in the ZIP file has a suspiciously high compression ratio.</li>
	 *                                          <li>An I/O error occurs during extraction.</li>
	 *                                        </ul>
	 * @throws IOException if an I/O error occurs while accessing the ZIP file or the target directory.
	 */
	public static void unzip(Path source, Path target) {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
			ZipEntry zipEntry;
			long totalUncompressedSize = 0;
			int entryCount = 0;
			byte[] buffer = new byte[2048];
			while ((zipEntry = zis.getNextEntry()) != null) {
				if (entryCount >= MAX_ENTRIES) {
					throw new InvalidIngestionFileException("ZIP file exceeds the maximum number of entries");
				}
				Path targetPath = zipSlipProtect(zipEntry, target);
				Files.createDirectories(targetPath.getParent());
				long totalSizeEntry = 0;
				int bytesRead;
				try (OutputStream out = Files.newOutputStream(targetPath, StandardOpenOption.CREATE)) {
					while ((bytesRead = zis.read(buffer)) > 0) {
						totalSizeEntry += bytesRead;
						totalUncompressedSize += bytesRead;
						if (totalUncompressedSize > MAX_UNCOMPRESSED_SIZE) {
							throw new InvalidIngestionFileException("ZIP file exceeds the maximum allowed uncompressed size");
						}
						if (zipEntry.getCompressedSize() > 0) {
							double compressionRatio = (double) totalSizeEntry / zipEntry.getCompressedSize();
							if (compressionRatio > MAX_COMPRESSION_RATIO) {
								throw new InvalidIngestionFileException("ZIP file contains an entry with excessive compression ratio");
							}
						}
						out.write(buffer, 0, bytesRead);
					}
				}
				zis.closeEntry();
				entryCount++;
			}
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
	private static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) {
		String checkedFilename = SecureFileUtils.checkFileName(zipEntry.getName());
		Path targetDirResolved = targetDir.resolve(checkedFilename);
		Path normalizePath = targetDirResolved.normalize();
		if (!normalizePath.startsWith(targetDir)) {
			throw new InvalidIngestionFileException("Bad zip entry: " + zipEntry.getName());
		}
		return normalizePath;
	}
}
