package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

	private Path zipFile;

	@BeforeEach
	void setup() throws IOException {
		zipFile = tempDir.resolve("test.zip");
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			addZipEntry(zos, "file1.txt", "This is the content of file1.");
			addZipEntry(zos, "file2.txt", "This is the content of file2.");
		}
	}

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
	void testUnzipValidArchive() throws IOException {
		Path outputDir = tempDir.resolve("output");

		assertDoesNotThrow(() -> FileUtils.unzip(zipFile, outputDir));
		assertTrue(Files.exists(outputDir.resolve("file1.txt")));
		assertTrue(Files.exists(outputDir.resolve("file2.txt")));

		String contentFile1 = Files.readString(outputDir.resolve("file1.txt"));
		String contentFile2 = Files.readString(outputDir.resolve("file2.txt"));
		assertEquals("This is the content of file1.", contentFile1);
		assertEquals("This is the content of file2.", contentFile2);
	}

	@Test
	void testUnzipWithExcessiveEntries() throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			for (int i = 0; i < 1100; i++) {
				addZipEntry(zos, "file" + i + ".txt", "Content of file " + i);
			}
		}
		Path outputDir = tempDir.resolve("output");
		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.unzip(zipFile, outputDir), "exceeds the maximum number of entries"
		);
	}

	@Test
	void testUnzipWithExcessiveUncompressedSize() throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			String largeContent = "A".repeat(60 * 1024 * 1024);
			addZipEntry(zos, "largefile.txt", largeContent);
		}
		Path outputDir = tempDir.resolve("output");
		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.unzip(zipFile, outputDir), "exceeds the maximum allowed uncompressed size"
		);
	}

	@Test
	void testUnzipWithZipSlipAttack() throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			zos.putNextEntry(new ZipEntry("../malicious.txt"));
			zos.write("Malicious content".getBytes());
			zos.closeEntry();
		}

		Path outputDir = tempDir.resolve("output");
		assertThrows(InvalidIngestionFileException.class,
			() -> FileUtils.unzip(zipFile, outputDir), "Bad zip entry"
		);
	}

	// Helper method to add entries to the ZIP file
	private void addZipEntry(ZipOutputStream zos, String entryName, String content) throws IOException {
		zos.putNextEntry(new ZipEntry(entryName));
		zos.write(content.getBytes());
		zos.closeEntry();
	}
}
