package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileAchiverServiceTest {
	private static final String TEST_PASSWORD = "mockPassword";

	@Mock
	private ZipFileService zipFileServiceMock;

	private IngestionFlowFileAchiverService service;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		service = new IngestionFlowFileAchiverService(tempDir.toString(), TEST_PASSWORD, zipFileServiceMock);
	}

	@Test
	void givenSuccessfullConditionsWhenCompressArchiveFileCleanUpThenOk(@TempDir Path sourceDir) throws Exception {
		//given
		Path file1 = Files.createFile(sourceDir.resolve("file1.txt"));
		Path file2 = Files.createFile(sourceDir.resolve("file2.txt"));
		List<Path> mockFiles = List.of(file1, file2);

		Path zipFilePath = Files.createFile(sourceDir.resolve("output.zip"));
		File mockZippedFile = zipFilePath.toFile();
		Path mockEncryptedFile = Files.copy(zipFilePath, sourceDir.resolve(zipFilePath.getFileName() + AESUtils.CIPHER_EXTENSION));

		when(zipFileServiceMock.zipper(zipFilePath, mockFiles)).thenReturn(mockZippedFile);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.encrypt(TEST_PASSWORD, mockZippedFile)).thenReturn(mockEncryptedFile.toFile());

			// when
			assertDoesNotThrow(
				() -> service.compressArchiveFileAndCleanUp(mockFiles, sourceDir.toString(), "output"));

			Path targetFile = tempDir.resolve(mockEncryptedFile.getFileName());
			//then
			assertTrue(Files.exists(targetFile));
			assertFalse(Files.exists(zipFilePath));
		}
	}

	@Test
	void givenExceptionOnEncryptionWhenCompressArchiveFileCleanUpThenThrowsIllegalStateException(@TempDir Path sourceDir) throws Exception {
		//given
		Path file1 = Files.createFile(sourceDir.resolve("file1.txt"));
		Path file2 = Files.createFile(sourceDir.resolve("file2.txt"));
		List<Path> mockFiles = List.of(file1, file2);

		Path zipFilePath = Files.createFile(sourceDir.resolve("output.zip"));
		File mockZippedFile = zipFilePath.toFile();
		Path mockEncryptedFile = Files.copy(zipFilePath, sourceDir.resolve(zipFilePath.getFileName() + AESUtils.CIPHER_EXTENSION));

		when(zipFileServiceMock.zipper(zipFilePath, mockFiles)).thenReturn(mockZippedFile);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.encrypt(TEST_PASSWORD, mockZippedFile)).thenThrow(IllegalStateException.class);

			// when then
			assertThrows(IllegalStateException.class,
				() -> service.compressArchiveFileAndCleanUp(mockFiles, sourceDir.toString(), "output"),
				"encryption failed");
		}
	}

	@Test
	void givenExceptionOnZippingWhenCompressArchiveFileCleanUpThenThrows(@TempDir Path sourceDir) throws Exception {
		//given
		Path file1 = Files.createFile(sourceDir.resolve("file1.txt"));
		Path file2 = Files.createFile(sourceDir.resolve("file2.txt"));
		List<Path> mockFiles = List.of(file1, file2);

		Path zipFilePath = Files.createFile(sourceDir.resolve("output.zip"));

		when(zipFileServiceMock.zipper(zipFilePath, mockFiles)).thenThrow(InvalidIngestionFileException.class);

			// when then
			assertThrows(InvalidIngestionFileException.class,
				() -> service.compressArchiveFileAndCleanUp(mockFiles, sourceDir.toString(), "output"),
				"zipping failed");
	}
}
