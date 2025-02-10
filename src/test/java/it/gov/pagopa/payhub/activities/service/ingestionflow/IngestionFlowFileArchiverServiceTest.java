package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileArchiverServiceTest {
	private static final String TEST_PASSWORD = "mockPassword";

	@Mock
	private ZipFileService zipFileServiceMock;

	private IngestionFlowFileArchiverService service;

	private final Path sharedDir = Path.of("build");
	private final Path targetDir = Path.of("build", "tmp");

	@BeforeEach
	void setUp() {
		service = new IngestionFlowFileArchiverService(sharedDir.toString(), "archive", TEST_PASSWORD, zipFileServiceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				zipFileServiceMock
		);
	}

//region test compressAndArchive
	@Test
	void givenSuccessfulConditionsWhenCompressAndArchiveThenOk(@TempDir Path sourceDir) throws Exception {
		//given
		Path file1 = Files.createFile(sourceDir.resolve("file1.txt"));
		Path file2 = Files.createFile(sourceDir.resolve("file2.txt"));
		List<Path> files = List.of(file1, file2);

		Path zipFilePath = Files.createFile(sourceDir.resolve("output.zip"));
		File mockZippedFile = zipFilePath.toFile();
		Path mockEncryptedFile = Files.copy(zipFilePath, sourceDir.resolve(zipFilePath.getFileName() + AESUtils.CIPHER_EXTENSION));

		when(zipFileServiceMock.zipper(zipFilePath, files)).thenReturn(mockZippedFile);
			assertTrue(Files.exists(mockEncryptedFile));

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.encrypt(TEST_PASSWORD, mockZippedFile)).thenReturn(mockEncryptedFile.toFile());
			// when
			service.compressAndArchive(files, zipFilePath, targetDir);

			//then
			assertFalse(zipFilePath.toFile().exists(), "zipped file should be deleted");
			assertFalse(mockEncryptedFile.toFile().exists(), "encrypted file should be deleted from source directory");
			assertTrue(targetDir.resolve("output.zip" + AESUtils.CIPHER_EXTENSION).toFile().exists(), "Success");
		}
	}

	@Test
	void givenExceptionOnEncryptionWhenCompressAndArchiveThenThrowsIllegalStateException(@TempDir Path sourceDir) throws Exception {
		//given
		Path file1 = Files.createFile(sourceDir.resolve("file1.txt"));
		Path file2 = Files.createFile(sourceDir.resolve("file2.txt"));
		List<Path> mockFiles = List.of(file1, file2);

		Path zipFilePath = Files.createFile(sourceDir.resolve("output.zip"));
		File mockZippedFile = zipFilePath.toFile();

		when(zipFileServiceMock.zipper(zipFilePath, mockFiles)).thenReturn(mockZippedFile);

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.encrypt(TEST_PASSWORD, mockZippedFile)).thenThrow(IllegalStateException.class);

			// when then
			assertThrows(IllegalStateException.class,
				() -> service.compressAndArchive(mockFiles, zipFilePath, targetDir),
				"encryption failed");
		}
	}

	@Test
	void givenExceptionOnZippingWhenCompressAndArchiveThenThrowsInvalidIngestionFileException(@TempDir Path sourceDir) throws Exception {
		//given
		Path file1 = Files.createFile(sourceDir.resolve("file1.txt"));
		Path file2 = Files.createFile(sourceDir.resolve("file2.txt"));
		List<Path> mockFiles = List.of(file1, file2);

		Path zipFilePath = Files.createFile(sourceDir.resolve("output.zip"));

		when(zipFileServiceMock.zipper(zipFilePath, mockFiles)).thenThrow(InvalidIngestionFileException.class);

		// when then
		assertThrows(InvalidIngestionFileException.class,
			() -> service.compressAndArchive(mockFiles, zipFilePath, targetDir),
			"zipping failed");
	}
//endregion

	@Test
	void whenArchiveThenOk(){
		// Given
		IngestionFlowFile ingestionFlowFile = IngestionFlowFileFaker.buildIngestionFlowFile()
				.organizationId(1L)
				.filePathName("path/to/file")
				.fileName("fileName.zip");

		Path srcPath = sharedDir.resolve("1").resolve("path/to/file");
		Path srcFile = srcPath.resolve(ingestionFlowFile.getFileName() + AESUtils.CIPHER_EXTENSION);
		Path archivePath = srcPath.resolve("archive");
		Path archiveFile = archivePath.resolve(srcFile.getFileName());

		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			// When
			service.archive(ingestionFlowFile);

			// Then
			mockedFiles.verify(() -> Files.createDirectories(archivePath));
			mockedFiles.verify(() -> Files.copy(
					srcFile,
					archiveFile,
					StandardCopyOption.REPLACE_EXISTING));
			mockedFiles.verify(() -> Files.deleteIfExists(srcFile));
		}
	}
}
