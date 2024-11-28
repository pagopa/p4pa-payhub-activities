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

class FileUtilsTest {
	@TempDir
	Path tempDir;

	@Test
	void validateFile_validFile_doesNotThrowException() throws IOException {
		Path validFile = Files.createFile(tempDir.resolve("validFile.txt"));

		assertDoesNotThrow(() -> FileUtils.validateFile(validFile), "Expected file is valid");
	}

	@Test
	void validateFile_nonExistentFile_throwsInvalidIngestionFileException() {
		Path nonExistentFile = tempDir.resolve("nonExistentFile.txt");

		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.validateFile(nonExistentFile), "Expected file not exist"
		);
	}

	@Test
	void validateFile_directoryInsteadOfFile_throwsInvalidIngestionFileException() {
		Path directory = tempDir.resolve("directory");
		assertTrue(directory.toFile().mkdir());

		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.validateFile(directory), "Expected file is not a regular file"
		);
	}

	@Test
	void testIsArchiveWithValidZipFile() throws IOException {
		Path validZip = tempDir.resolve("valid.zip");
		Files.write(validZip, new byte[]{0x50, 0x4B, 0x03, 0x04}, StandardOpenOption.CREATE_NEW);

		assertTrue(FileUtils.isArchive(validZip), "Expected file to be recognized as a valid ZIP archive");
	}

	@Test
	void testIsArchiveWithInvalidZipFile() throws IOException {
		Path invalidZip = tempDir.resolve("invalid.zip");
		Files.write(invalidZip, new byte[]{0x00, 0x00, 0x00, 0x00}, StandardOpenOption.CREATE_NEW);

		assertFalse(FileUtils.isArchive(invalidZip), "Expected file to not be recognized as a valid ZIP archive");
	}

	@Test
	void testIsArchiveWithEmptyFile() throws IOException {
		Path emptyFile = tempDir.resolve("empty.zip");
		Files.createFile(emptyFile);

		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.isArchive(emptyFile), "Expected InvalidIngestionFileException for an empty file");
	}

	@Test
	void testIsArchiveWithNonExistentFile() {
		Path nonExistentFile = tempDir.resolve("nonexistent.zip");

		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.isArchive(nonExistentFile), "Expected InvalidIngestionFileException for a non-existent file");
	}

	@Test
	void testUnzipFolderSuccess() throws IOException {
		Path zipFile = tempDir.resolve("test.zip");
		Path extractedDir = tempDir.resolve("extracted");

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
			ZipEntry fileEntry = new ZipEntry("testfile.txt");
			zos.putNextEntry(fileEntry);
			zos.write("Hello, World!".getBytes());
			zos.closeEntry();
		}
		FileUtils.unzip(zipFile, extractedDir);
		
		Path extractedFile = extractedDir.resolve("testfile.txt");
		assertTrue(Files.exists(extractedFile), "Extracted file should exist");
		assertEquals("Hello, World!", Files.readString(extractedFile), "Extracted file content should match");
	}

	@Test
	void testUnzipFolderWithDirectoryEntry() throws IOException {
		// Create a ZIP file containing a directory entry
		Path zipFile = tempDir.resolve("test_with_dir.zip");
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
			zos.putNextEntry(new ZipEntry("folder/")); // Directory entry
			zos.closeEntry();
		}

		assertThrows(
			InvalidIngestionFileException.class,
			() -> FileUtils.unzip(zipFile, tempDir), "Expected exception for ZIP containing directories."
		);
	}

	@Test
	void testUnzipFolderWithNonExistentSource() {
		Path nonExistentZip = tempDir.resolve("nonexistent.zip");

		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.unzip(nonExistentZip, tempDir), "Expected exception for non-existent ZIP file."
		);
	}
}
