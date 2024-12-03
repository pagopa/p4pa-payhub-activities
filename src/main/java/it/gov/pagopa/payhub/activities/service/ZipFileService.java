package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.io.*;
=======
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
>>>>>>> P4ADEV-1545-FdRIngestionActity-implementation-retrieval-via-query-file-validation
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
<<<<<<< HEAD
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
=======
>>>>>>> P4ADEV-1545-FdRIngestionActity-implementation-retrieval-via-query-file-validation

/**
 * Service class for performing common file operations, including
 * validation, validate ZIP entry names and extraction of ZIP files.
 * <p>
 * This class provides secure methods to handle files, ensuring safety
 * against potential vulnerabilities, such as ZIP Slip attacks.
 * </p>
 */
@Lazy
@Slf4j
@Service
public class ZipFileService {
	/** Maximum number of entries to extract from the ZIP file */
	private final int maxEntries;
	/** Maximum total size (in bytes) of all uncompressed data, for example 50MB */
	private final long maxUncompressedSize;
	/** Maximum allowed compression ratio (compressed size / uncompressed size), for example 10% */
	private final double maxCompressionRatio;

	public ZipFileService(@Value("${zip-file.max-entries:1000}") int maxEntries,
						  @Value("${zip-file.max-uncompressed-size:50 * 1024 * 1024}") long maxUncompressedSize,
						  @Value("${zip-file.max-compression-ratio:0.1}") double maxCompressionRatio) {
		this.maxEntries = maxEntries;
		this.maxUncompressedSize = maxUncompressedSize;
		this.maxCompressionRatio = maxCompressionRatio;
	}

	/**
	 * Extracts the contents of a ZIP file to a specified target directory and returns
	 * a list of paths to the extracted files.
	 *
	 * This method performs secure extraction by validating:
	 * <ul>
	 *   <li>The number of entries in the ZIP file does not exceed a predefined threshold.</li>
	 *   <li>The total uncompressed size of all extracted data does not exceed a maximum allowed size.</li>
	 *   <li>The compression ratio of each entry is within acceptable limits to mitigate ZIP bomb attacks.</li>
	 *   <li>Paths are validated to prevent ZIP Slip attacks.</li>
	 * </ul>
	 *
	 * If any validation fails during extraction, the method will throw an exception
	 * and terminate the process.
	 *
	 * @param source the path to the ZIP file to be extracted
	 * @param target the target directory where the contents will be extracted
	 * @return a list of {@link Path} objects representing the extracted files
	 * @throws InvalidIngestionFileException if:
	 *         <ul>
	 *           <li>The ZIP file contains an invalid entry name (ZIP Slip vulnerability).</li>
	 *           <li>The ZIP file exceeds the maximum allowed uncompressed size.</li>
	 *           <li>The ZIP file contains an excessive number of entries.</li>
	 *           <li>An entry in the ZIP file has a suspiciously high compression ratio.</li>
	 *           <li>An I/O error occurs during extraction.</li>
	 *         </ul>
	 */
	public List<Path> unzip(Path source, Path target) {
		List<Path> extractedPaths = new ArrayList<>();

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
			long totalUncompressedSize = 0;
			int entryCount = 0;

			ZipEntry zipEntry;
			while ((zipEntry = zis.getNextEntry()) != null) {
				validateEntryCount(++entryCount);
				Path targetPath = validateAndPrepareTargetPath(zipEntry, target);

				long totalSizeEntry = 0;
				byte[] buffer = new byte[2048];

				try (OutputStream out = Files.newOutputStream(targetPath, StandardOpenOption.CREATE)) {
					int bytesRead;
					while ((bytesRead = zis.read(buffer)) > 0) {
						totalSizeEntry += bytesRead;
						totalUncompressedSize += bytesRead;

						validateUncompressedSize(totalUncompressedSize);
						validateCompressionRatio(zipEntry, totalSizeEntry);

						out.write(buffer, 0, bytesRead);
					}
				}
				extractedPaths.add(targetPath);
				zis.closeEntry();
			}
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error while unzipping file: " + source);
		}
		return extractedPaths;
	}

	/**
	 * Extracts the contents of a ZIP file to its own directory and returns a list of paths
	 * to the extracted files.
	 *
	 * This method uses the directory of the provided ZIP file as the default extraction location.
	 * The contents of the ZIP file will be extracted into a subdirectory named after the ZIP file
	 * (without its extension) within the same directory.
	 *
	 * Internally, this method delegates the extraction process to {@link #unzip(Path, Path)}.
	 *
	 * @param path the path to the ZIP file to be extracted
	 * @return a list of {@link Path} objects representing the extracted files
	 * @throws InvalidIngestionFileException if:
	 *         <ul>
	 *           <li>The ZIP file contains an invalid entry name (ZIP Slip vulnerability).</li>
	 *           <li>The ZIP file exceeds the maximum allowed uncompressed size.</li>
	 *           <li>The ZIP file contains an excessive number of entries.</li>
	 *           <li>An entry in the ZIP file has a suspiciously high compression ratio.</li>
	 *           <li>An I/O error occurs during extraction.</li>
	 *         </ul>
	 */
	public List<Path> unzip(Path path) {
		return unzip(path, path);
	}

	/**
	 * Validates the entry count against the maximum allowed entries.
	 *
	 * @param entryCount the current entry count.
	 * @throws InvalidIngestionFileException if the entry count exceeds the maximum allowed.
	 */
	private void validateEntryCount(int entryCount) {
		if (entryCount > maxEntries) {
			throw new InvalidIngestionFileException("ZIP file exceeds the maximum number of entries");
		}
	}

	/**
	 * Validates and prepares the target path for the current ZIP entry.
	 *
	 * @param zipEntry the ZIP entry to process.
	 * @param target the target directory for extraction.
	 * @return the validated and normalized target path.
	 * @throws InvalidIngestionFileException if the ZIP entry is invalid.
	 */
	private Path validateAndPrepareTargetPath(ZipEntry zipEntry, Path target) throws IOException {
		checkFileName(zipEntry);
		Path targetPath = zipSlipProtect(zipEntry, target);
		Files.createDirectories(targetPath.getParent());
		return targetPath;
	}

	/**
	 * Validates the total uncompressed size against the maximum allowed size.
	 *
	 * @param totalUncompressedSize the current total uncompressed size.
	 * @throws InvalidIngestionFileException if the uncompressed size exceeds the maximum allowed.
	 */
	private void validateUncompressedSize(long totalUncompressedSize) {
		if (totalUncompressedSize > maxUncompressedSize) {
			throw new InvalidIngestionFileException("ZIP file exceeds the maximum allowed uncompressed size");
		}
	}

	/**
	 * Validates the compression ratio of the current ZIP entry.
	 *
	 * @param zipEntry the ZIP entry to validate.
	 * @param totalSizeEntry the current uncompressed size of the entry.
	 * @throws InvalidIngestionFileException if the compression ratio exceeds the maximum allowed.
	 */
	private void validateCompressionRatio(ZipEntry zipEntry, long totalSizeEntry) {
		if (zipEntry.getCompressedSize() > 0) {
			double compressionRatio = (double) totalSizeEntry / zipEntry.getCompressedSize();
			if (compressionRatio > maxCompressionRatio) {
				throw new InvalidIngestionFileException("ZIP file contains an entry with excessive compression ratio");
			}
		}
	}

	/**
	 * Protects against ZIP Slip vulnerability by validating the path of an extracted entry.
	 *
	 * @param zipEntry the ZIP entry to validate.
	 * @param targetDir the target directory for extraction.
	 * @return a safe, normalized path towards zipEntry within the target directory.
	 * @throws InvalidIngestionFileException if the ZIP entry is outside the target directory.
	 */
	private Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) {
		String checkedFilename = checkFileName(zipEntry.getName());
		Path targetDirResolved = targetDir.resolve(checkedFilename);
		Path normalizePath = targetDirResolved.normalize();
		if (!normalizePath.startsWith(targetDir)) {
			throw new InvalidIngestionFileException("Bad zip entry: " + zipEntry.getName());
		}
		return normalizePath;
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
	private static String checkFileName(String fileName) throws IllegalArgumentException {
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
	private static ZipEntry checkFileName(ZipEntry entry) throws IllegalArgumentException {
		checkFileName(entry.getName());
		return entry;
	}
<<<<<<< HEAD

	/**
	 * Moves the specified file to the target directory, keeping the original file name.
	 *
	 * This method performs the following operations:
	 * <ul>
	 *   <li>Copies the file to the target directory.</li>
	 *   <li>Deletes the original file after a successful copy.</li>
	 * </ul>
	 *
	 * If the target directory already contains a file with the same name, the existing file
	 * will be replaced.
	 *
	 * @param file   the file to be moved
	 * @param target the target directory where the file will be moved
	 */
	public static void moveFile(File file, Path target) {
		moveFile(file, target, file.getName());
	}

	/**
	 * Moves the specified file to the target directory with a new file name.
	 *
	 * This method performs the following operations:
	 * <ul>
	 *   <li>Copies the file to the target directory with the specified new file name.</li>
	 *   <li>Deletes the original file after a successful copy.</li>
	 * </ul>
	 *
	 * If the target directory already contains a file with the same new name, the existing file
	 * will be replaced.
	 *
	 * @param originalFile the file to be moved
	 * @param target       the target directory where the file will be moved
	 * @param newFileName  the new name for the file in the target directory
	 * @throws IOException if an I/O error occurs during the file move operation
	 *                      (e.g., the file or directory does not exist, or the file cannot be copied).
	 */
	public static void moveFile(File originalFile, Path target, String newFileName) {
		try {
			Files.copy(originalFile.toPath(), target.resolve(newFileName), REPLACE_EXISTING);
			Files.delete(originalFile.toPath());
		} catch (IOException e) {
			System.out.println(e);
			throw new InvalidIngestionFileException("Error while moving: " + originalFile);
		}
	}

	/**
	 * Compresses the specified files into a single ZIP archive at the given ZIP file path.
	 *
	 * This method performs the following operations:
	 * <ul>
	 *   <li>Creates a ZIP archive at the specified path.</li>
	 *   <li>Compresses each specified file into the ZIP archive.</li>
	 * </ul>
	 *
	 * @param zipFilePath the path where the ZIP file will be created, including the desired filename
	 * @param filesToZip  a list of {@link Path} objects representing the files to be compressed
	 * @return a {@link File} object representing the created ZIP archive
	 * @throws IOException if any I/O error occurs during compression
	 *                      (e.g., file not found, read/write issues, invalid paths).
	 * @throws IllegalArgumentException if the file list is null or empty, or if the ZIP file path is invalid.
	 */
	public File zipper(Path zipFilePath, List<Path> filesToZip) {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
			for (Path file : filesToZip) {
				addFileToZip(zos, file);
			}
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error while zipping: " + zipFilePath);
		}
		return zipFilePath.toFile();
	}

	/**
	 * Adds a file to the ZIP archive securely.
	 *
	 * @param zos the {@link ZipOutputStream} used for writing the ZIP archive
	 * @param file the file to be added to the archive
	 * @throws IOException if an I/O error occurs while adding the file to the archive
	 */
	private void addFileToZip(ZipOutputStream zos, Path file) throws IOException {
		String checkedFilename = checkFileName(file.getFileName().toString());
		ZipEntry zipEntry = new ZipEntry(checkedFilename);
		zos.putNextEntry(zipEntry);
		Files.copy(file, zos);
		zos.closeEntry();
	}
=======
>>>>>>> P4ADEV-1545-FdRIngestionActity-implementation-retrieval-via-query-file-validation
}