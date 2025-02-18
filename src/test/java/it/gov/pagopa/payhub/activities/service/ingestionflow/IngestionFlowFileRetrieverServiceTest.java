package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
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

import java.io.File;
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
	private static final String SHARED_PATH = "/tmp";
	private static final String TEMPORARY_PATH = "/tmp";

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
		service = new IngestionFlowFileRetrieverService(SHARED_PATH, TEMPORARY_PATH, TEST_CIPHER_PSW, fileValidatorService, zipFileService);
		zipFile = tempDir.resolve("encryptedFile.zip");
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			addZipEntry(zos, "file1.txt", "This is the content of file1.");
			addZipEntry(zos, "file2.txt", "This is the content of file2.");
		}
	}

	@Test
	void testRetrieveFile_successfulFlow() {
		//Given
		Long organizationId = 0L;
		Path sourcePath = zipFile.getParent();
		String filename = zipFile.getFileName().toString();
		Path workingPath = Path.of(TEMPORARY_PATH)
				.resolve(String.valueOf(organizationId))
				.resolve(sourcePath.subpath(0, sourcePath.getNameCount()));
		Path zipFileInWorkingDirectory = workingPath.resolve(filename);
		List<Path> unzippedPaths = List.of(workingPath.resolve("file1.txt"), workingPath.resolve("file2.txt"));

		doNothing().when(fileValidatorService).validateFile(sourcePath.resolve(filename + AESUtils.CIPHER_EXTENSION));
		doReturn(true).when(fileValidatorService).isArchive(zipFileInWorkingDirectory);
		doReturn(true).when(fileValidatorService).isZipFileByExtension(zipFileInWorkingDirectory);
		when(zipFileService.unzip(zipFileInWorkingDirectory)).thenReturn(unzippedPaths);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, zipFileInWorkingDirectory.toFile(), workingPath.toFile()))
				.then(invocation -> null);

			// when
			List<Path> result = service.retrieveAndUnzipFile(organizationId, sourcePath, filename);

			// Then
			assertNotNull(result);
			assertEquals(2, result.size());
			assertTrue(result.stream().anyMatch(path -> path.getFileName().toString().equals("file1.txt")));
			assertTrue(result.stream().anyMatch(path -> path.getFileName().toString().equals("file2.txt")));
			assertEquals(unzippedPaths, result);
		}
	}

	@Test
	void testRetrieveFile_successfulFlow_notZipped() throws IOException {
		//Given
		Long organizationId = 0L;
		File nonZippedFileFile = File.createTempFile("testRetrieveFile_successfulFlow_notZipped", ".tmp");
		nonZippedFileFile.deleteOnExit();
		Path nonZippedFile = nonZippedFileFile.toPath();
		Path sourcePath = nonZippedFile.getParent();
		String filename = nonZippedFile.getFileName().toString();
		Path workingPath = Path.of(TEMPORARY_PATH)
			.resolve(String.valueOf(organizationId))
			.resolve(sourcePath.subpath(0, sourcePath.getNameCount()));
		Path zipFileInWorkingDirectory = workingPath.resolve(filename);
		List<Path> unzippedPaths = List.of(workingPath.resolve(filename));

		doNothing().when(fileValidatorService).validateFile(sourcePath.resolve(filename + AESUtils.CIPHER_EXTENSION));
		doReturn(false).when(fileValidatorService).isZipFileByExtension(zipFileInWorkingDirectory);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, zipFileInWorkingDirectory.toFile(), workingPath.toFile()))
				.then(invocation -> null);

			// when
			List<Path> result = service.retrieveAndUnzipFile(organizationId, sourcePath, filename);

			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			assertEquals(unzippedPaths, result);
		}
	}

	@Test
	void testRetrieveFile_validationFails() {
		//Given
		Long organizationId = 0L;
		Path sourcePath = zipFile.getParent();
		String filename = zipFile.getFileName().toString();

		doThrow(new InvalidIngestionFileException("File validation failed")).when(fileValidatorService).validateFile(sourcePath.resolve(filename + AESUtils.CIPHER_EXTENSION));

		//When & Then
		assertThrows(InvalidIngestionFileException.class,
			() -> service.retrieveAndUnzipFile(organizationId, sourcePath, filename), "File validation failed");
	}

	@Test
	void testRetrieveFile_zipValidationFails() {
		//Given
		Long organizationId = 0L;
		Path sourcePath = zipFile.getParent();
		String filename = zipFile.getFileName().toString();
		Path workingPath = Path.of(TEMPORARY_PATH)
				.resolve(String.valueOf(organizationId))
				.resolve(sourcePath.subpath(0, sourcePath.getNameCount()));
		Path zipFileInWorkingDirectory = workingPath.resolve(filename);

		doNothing().when(fileValidatorService).validateFile(sourcePath.resolve(filename + AESUtils.CIPHER_EXTENSION));
		doThrow(new InvalidIngestionFileException("ZIP validation failed")).when(fileValidatorService).isArchive(zipFileInWorkingDirectory);
		doReturn(true).when(fileValidatorService).isZipFileByExtension(zipFileInWorkingDirectory);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.decrypt(TEST_CIPHER_PSW, zipFileInWorkingDirectory.toFile(), workingPath.toFile()))
				.then(invocation -> null);

			//When & Then
			assertThrows(InvalidIngestionFileException.class,
				() -> service.retrieveAndUnzipFile(organizationId, sourcePath, filename), "ZIP validation failed");
		}
	}

	/** Helper method to add entries to the ZIP file */
	private static void addZipEntry(ZipOutputStream zos, String entryName, String content) throws IOException {
		zos.putNextEntry(new ZipEntry(entryName));
		zos.write(content.getBytes());
		zos.closeEntry();
	}
}