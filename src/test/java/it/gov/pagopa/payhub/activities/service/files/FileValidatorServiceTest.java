package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorServiceTest {
	@TempDir
	private Path tempDir;

	private FileValidatorService service;

	@BeforeEach
	void setup() {
		service = new FileValidatorService();
	}

	@Test
	void validateFile_validFile_doesNotThrowException() throws IOException {
		Path validFile = Files.createFile(tempDir.resolve("validFile.txt"));

		assertDoesNotThrow(() -> service.validateFile(validFile), "Expected file is valid");
	}

	@Test
	void validateFile_nonExistentFile_throwsInvalidIngestionFileException() {
		Path nonExistentFile = tempDir.resolve("nonExistentFile.txt");

		assertThrows(InvalidIngestionFileException.class,
			() -> service.validateFile(nonExistentFile), "Expected file not exist"
		);
	}

	@Test
	void validateFile_directoryInsteadOfFile_throwsInvalidIngestionFileException() {
		Path directory = tempDir.resolve("directory");
		assertTrue(directory.toFile().mkdir());

		assertThrows(InvalidIngestionFileException.class,
			() -> service.validateFile(directory), "Expected file is not a regular file"
		);
	}

	@Test
	void testIsArchiveWithValidZipFile() throws IOException {
		Path validZip = tempDir.resolve("valid.zip");
		Files.write(validZip, new byte[]{0x50, 0x4B, 0x03, 0x04});

		assertTrue(service.isArchive(validZip), "Expected file to be recognized as a valid ZIP archive");
	}

	@Test
	void testIsArchiveWithInvalidZipFile() throws IOException {
		Path invalidZip = tempDir.resolve("invalid.zip");
		Files.write(invalidZip, new byte[]{0x00, 0x00, 0x00, 0x00});

		assertFalse(service.isArchive(invalidZip), "Expected file to not be recognized as a valid ZIP archive");
	}

	@Test
	void testIsArchiveWithEmptyFile() throws IOException {
		Path emptyFile = tempDir.resolve("empty.zip");
		Files.createFile(emptyFile);

		assertThrows(InvalidIngestionFileException.class,
			() -> service.isArchive(emptyFile), "Expected InvalidIngestionFileException for an empty file");
	}

	@Test
	void givenNotZipFileWhenIsZipFileByExtensionThenReturnFalse() {
		Path notZipFile = Path.of("notZipFile.txt");
		assertFalse(service.isZipFileByExtension(notZipFile), "Expected file to not be recognized as a ZIP file");
	}

	@Test
	void givenZipFileWhenIsZipFileByExtensionThenReturnTrue() {
		Path zipFile = Path.of("zipFile.zip");
		assertTrue(service.isZipFileByExtension(zipFile), "Expected file to be recognized as a ZIP file");
	}
}