package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {
	@TempDir
	Path tempDir;

	@Test
	void testIsArchiveWithValidZipFile() throws IOException {
		Path validZip = tempDir.resolve("valid.zip");
		Files.write(validZip, new byte[]{0x50, 0x4B, 0x03, 0x04}, StandardOpenOption.CREATE_NEW);

		assertTrue(FileUtils.isArchive(validZip), "Expected file to be recognized as a valid ZIP archive.");
	}

	@Test
	void testIsArchiveWithInvalidZipFile() throws IOException {
		Path invalidZip = tempDir.resolve("invalid.zip");
		Files.write(invalidZip, new byte[]{0x00, 0x00, 0x00, 0x00}, StandardOpenOption.CREATE_NEW);

		assertFalse(FileUtils.isArchive(invalidZip), "Expected file to not be recognized as a valid ZIP archive.");
	}

	@Test
	void testIsArchiveWithEmptyFile() throws IOException {
		Path emptyFile = tempDir.resolve("empty.zip");
		Files.createFile(emptyFile);

		Exception exception = assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.isArchive(emptyFile),
			"Expected InvalidIngestionFileException for an empty file.");
		assertEquals("Invalid zip file", exception.getMessage());
	}

	@Test
	void testIsArchiveWithNonExistentFile() {
		Path nonExistentFile = tempDir.resolve("nonexistent.zip");

		Exception exception = assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.isArchive(nonExistentFile),
			"Expected InvalidIngestionFileException for a non-existent file.");
		assertEquals("Invalid zip file", exception.getMessage());
	}

	@Test
	void testUnzipFolderSuccess() throws IOException {
		// Create a sample ZIP file
		Path zipFile = tempDir.resolve("test.zip");
		Path extractedDir = tempDir.resolve("extracted");

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
			ZipEntry fileEntry = new ZipEntry("testfile.txt");
			zos.putNextEntry(fileEntry);
			zos.write("Hello, World!".getBytes());
			zos.closeEntry();
		}

		// Call the method to test
		FileUtils.unzip(zipFile, extractedDir);

		// Assert the file was extracted correctly
		Path extractedFile = extractedDir.resolve("testfile.txt");
		assertTrue(Files.exists(extractedFile), "Extracted file should exist.");
		assertEquals("Hello, World!", Files.readString(extractedFile), "Extracted file content should match.");
	}

	@Test
	void testUnzipFolderWithDirectoryEntry() throws IOException {
		// Create a ZIP file containing a directory entry
		Path zipFile = tempDir.resolve("test_with_dir.zip");
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
			zos.putNextEntry(new ZipEntry("folder/")); // Directory entry
			zos.closeEntry();
		}

		// Call the method to test and assert exception
		InvalidIngestionFileException exception = assertThrows(
			InvalidIngestionFileException.class,
			() -> FileUtils.unzip(zipFile, tempDir),
			"Expected exception for ZIP containing directories."
		);
		assertEquals("ZIP file contains directories, but only files are expected", exception.getMessage());
	}

	@Test
	void testUnzipFolderWithNonExistentSource() {
		// Call the method with a non-existent source file
		Path nonExistentZip = tempDir.resolve("nonexistent.zip");

		InvalidIngestionFileException exception = assertThrows(
			InvalidIngestionFileException.class,
			() -> FileUtils.unzip(nonExistentZip, tempDir),
			"Expected exception for non-existent ZIP file."
		);
		assertTrue(exception.getMessage().contains("Error while unzipping file"));
	}
}
