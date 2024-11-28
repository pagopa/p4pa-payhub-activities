package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.IngestionFileHandlerService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class IngestionFileHandlerServiceTest {

	private static final String TEST_PATH = "some/path";
	private static final String TEST_FILENAME = "testfile.zip";
	private static final String TEST_CIPHER_PSW = "testPassword";

	private IngestionFileHandlerService ingestionFileHandlerService;


	@BeforeEach
	void setUp() {
		ingestionFileHandlerService = new IngestionFileHandlerService(TEST_CIPHER_PSW);
	}

	@Test
	void testSetUpProcessSuccess() throws IOException {
		// Given
		String relativePath = TEST_PATH;
		String filename = TEST_FILENAME + AESUtils.CIPHER_EXTENSION;
		Path relativePathDir = Paths.get(relativePath);
		Path encryptedFilePath = relativePathDir.resolve(filename);
		Path temporaryZipFilePath = relativePathDir.resolve("TEMP/testfile.zip");
		Path outputUnzippedPath = relativePathDir.resolve("TEMP/testfile.xml");

		// Mock static methods
		try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class);
		     MockedStatic<FileUtils> fileUtilsMock = Mockito.mockStatic(FileUtils.class)) {

			aesUtilsMock.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, encryptedFilePath.toFile(), temporaryZipFilePath.toFile()))
				.thenAnswer(invocation -> null);
			fileUtilsMock.when(() -> FileUtils.isArchive(temporaryZipFilePath))
				.thenReturn(true);
			fileUtilsMock.when(() -> FileUtils.unzip(temporaryZipFilePath, outputUnzippedPath))
				.thenAnswer(invocation -> null);

			// When
			Path result = ingestionFileHandlerService.setUpProcess(relativePath, filename);

			// Then
			assertEquals(outputUnzippedPath, result, "The output path should match the expected path.");
		}
	}

	@Test
	void testSetUpProcessWithInvalidZip() {
		// Given
		String relativePath = TEST_PATH;
		String filename = TEST_FILENAME + AESUtils.CIPHER_EXTENSION;
		Path relativePathDir = Paths.get(relativePath);
		Path encryptedFilePath = relativePathDir.resolve(filename);
		Path temporaryZipFilePath = Paths.get(relativePath, "TEMP", TEST_FILENAME);

		try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class);
		     MockedStatic<FileUtils> fileUtilsMock = Mockito.mockStatic(FileUtils.class)) {

			aesUtilsMock.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, encryptedFilePath.toFile(), temporaryZipFilePath.toFile()))
				.thenAnswer(invocation -> null); // Simula decrittazione
			fileUtilsMock.when(() -> FileUtils.isArchive(eq(temporaryZipFilePath)))
				.thenThrow(new InvalidIngestionFileException("Invalid zip file")); // Simula errore

			// When Then
			assertThrows(InvalidIngestionFileException.class,
				() -> ingestionFileHandlerService.setUpProcess(relativePath, filename), "Expected exception for invalid zip file."
			);
		}
	}

	@Test
	void testSetUpProcessWithDecryptionFailure() {
		// Given
		String relativePath = TEST_PATH;
		String filename = TEST_FILENAME + AESUtils.CIPHER_EXTENSION;
		Path relativePathDir = Paths.get(relativePath);
		Path encryptedFilePath = relativePathDir.resolve(filename);
		Path temporaryZipFilePath = Paths.get(relativePath, "TEMP", TEST_FILENAME);

		try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {

			aesUtilsMock.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, encryptedFilePath.toFile(), temporaryZipFilePath.toFile()))
				.thenThrow(new IllegalStateException("Decryption failed"));

			// When Then
			assertThrows(IllegalStateException.class, () -> ingestionFileHandlerService.setUpProcess(relativePath, filename),
				"Expected exception for decryption failure."
			);
		}
	}
}
