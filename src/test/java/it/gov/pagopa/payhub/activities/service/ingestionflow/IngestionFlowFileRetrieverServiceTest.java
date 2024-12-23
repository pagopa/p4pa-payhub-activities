package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.FileValidatorService;
import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileRetrieverServiceTest {
	private static final String TEST_CIPHER_PSW = "testPassword";
	private static final String TEMPORARY_PATH = "/tmp/";

	@Mock
	private FileValidatorService fileValidatorService;

	@Mock
	private ZipFileService zipFileService;

	private IngestionFlowFileRetrieverService service;

	private Path zipFile;

	@TempDir
	private Path tempDir;

	@BeforeEach
	void setup() throws IOException {
		service = new IngestionFlowFileRetrieverService(TEMPORARY_PATH, TEST_CIPHER_PSW, fileValidatorService, zipFileService);
		zipFile = tempDir.resolve("encryptedFile.zip.cipher");
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			addZipEntry(zos, "file1.txt", "This is the content of file1.");
			addZipEntry(zos, "file2.txt", "This is the content of file2.");
		}
	}

	@Test
	void testRetrieveFile_successfulFlow() throws IOException {
		//Given
		Path sourcePath = zipFile.getParent();
		String filename = zipFile.getFileName().toString();
		Path workingPath = Path.of(TEMPORARY_PATH).resolve(sourcePath.subpath(0, sourcePath.getNameCount()));
		Path zipFilePath = workingPath.resolve(filename.replace(AESUtils.CIPHER_EXTENSION, ""));
		List<Path> unzippedPaths = List.of(workingPath.resolve("file1.txt"), workingPath.resolve("file2.txt"));

		doNothing().when(fileValidatorService).validateFile(zipFile);
		doReturn(true).when(fileValidatorService).isArchive(zipFilePath);
		when(zipFileService.unzip(workingPath)).thenReturn(unzippedPaths);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, zipFilePath.toFile(), workingPath.toFile()))
				.then(invocation -> null);

			// when
			List<Path> result = service.retrieveAndUnzipFile(sourcePath, filename);

			// Then
			assertNotNull(result);
			assertEquals(2, result.size());
			assertTrue(result.stream().anyMatch(path -> path.getFileName().toString().equals("file1.txt")));
			assertTrue(result.stream().anyMatch(path -> path.getFileName().toString().equals("file2.txt")));
			assertEquals(unzippedPaths, result);
		}
	}

	@Test
	void testRetrieveFile_validationFails() {
		//Given
		Path sourcePath = zipFile.getParent();
		String filename = zipFile.getFileName().toString();

		doThrow(new InvalidIngestionFileException("File validation failed")).when(fileValidatorService).validateFile(zipFile);

		//When & Then
		assertThrows(InvalidIngestionFileException.class,
			() -> service.retrieveAndUnzipFile(sourcePath, filename), "File validation failed");
	}

	@Test
	void testRetrieveFile_zipValidationFails() {
		//Given
		Path sourcePath = zipFile.getParent();
		String filename = zipFile.getFileName().toString();
		Path workingPath = Path.of(TEMPORARY_PATH).resolve(sourcePath.subpath(0, sourcePath.getNameCount()));
		Path zipFilePath = workingPath.resolve(filename.replace(AESUtils.CIPHER_EXTENSION, ""));

		doNothing().when(fileValidatorService).validateFile(zipFile);
		doThrow(new InvalidIngestionFileException("ZIP validation failed")).when(fileValidatorService).isArchive(zipFilePath);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, zipFilePath.toFile(), workingPath.toFile()))
				.then(invocation -> null);

			//When & Then
			assertThrows(InvalidIngestionFileException.class,
				() -> service.retrieveAndUnzipFile(sourcePath, filename), "ZIP validation failed");
		}
	}

	/** Helper method to add entries to the ZIP file */
	private static void addZipEntry(ZipOutputStream zos, String entryName, String content) throws IOException {
		zos.putNextEntry(new ZipEntry(entryName));
		zos.write(content.getBytes());
		zos.closeEntry();
	}
}