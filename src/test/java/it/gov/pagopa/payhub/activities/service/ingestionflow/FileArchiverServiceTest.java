package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.files.ZipFileService;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileArchiverServiceTest {
	private static final String TEST_PASSWORD = "mockPassword";

	@Mock
	private ZipFileService zipFileServiceMock;

	private FileArchiverService service;

	private final Path sharedDir = Path.of("build");
	private final Path targetDir = Path.of("build", "tmp");

	@BeforeEach
	void setUp() {
		service = new FileArchiverService(sharedDir.toString(), "archive", TEST_PASSWORD, zipFileServiceMock);
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
			Long fileSize = service.compressAndArchive(files, zipFilePath, targetDir);

			//then
			assertNotNull(fileSize);
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

	@Test
	void givenZipFilePathWhenCreateZipOutputStreamThenCreatesParentDirectoryAndZipFile(@TempDir Path sourceDir) throws Exception {
		// given
		Path zipFilePath = sourceDir.resolve("nested").resolve("output.zip");

		// when
		try (ZipOutputStream zipOutputStream = service.createZipOutputStream(zipFilePath)) {
			ZipEntry entry = new ZipEntry("file.txt");
			zipOutputStream.putNextEntry(entry);
			zipOutputStream.write("content".getBytes(StandardCharsets.UTF_8));
			zipOutputStream.closeEntry();
		}

		// then
		assertTrue(Files.exists(zipFilePath.getParent()));
		assertTrue(Files.exists(zipFilePath));
		assertTrue(Files.size(zipFilePath) > 0);
	}

	@Test
	void givenRegularFileWhenAddToZipThenAddsFileAndDeletesSource(@TempDir Path sourceDir) throws Exception {
		// given
		Path fileToAdd = sourceDir.resolve("notice.pdf");
		Files.writeString(fileToAdd, "pdf-content");

		Path zipFilePath = sourceDir.resolve("output.zip");

		// when
		try (ZipOutputStream zipOutputStream = service.createZipOutputStream(zipFilePath)) {
			service.addToZip(zipOutputStream, fileToAdd, "notice.pdf");
		}

		// then
		assertFalse(Files.exists(fileToAdd), "source file should be deleted after being added to zip");
		assertTrue(Files.exists(zipFilePath));

		try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFilePath))) {
			ZipEntry entry = zipInputStream.getNextEntry();

			assertNotNull(entry);
			assertEquals("notice.pdf", entry.getName());

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			zipInputStream.transferTo(outputStream);

			assertEquals("pdf-content", outputStream.toString(StandardCharsets.UTF_8));

			assertNull(zipInputStream.getNextEntry(), "zip should contain only one entry");
		}
	}

	@Test
	void givenNotRegularFileWhenAddToZipThenDoesNothing(@TempDir Path sourceDir) throws Exception {
		// given
		Path missingFile = sourceDir.resolve("missing.pdf");
		Path zipFilePath = sourceDir.resolve("output.zip");

		// when
		try (ZipOutputStream zipOutputStream = service.createZipOutputStream(zipFilePath)) {
			service.addToZip(zipOutputStream, missingFile, "missing.pdf");
		}

		// then
		assertFalse(Files.exists(missingFile));
		assertTrue(Files.exists(zipFilePath));

		try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFilePath))) {
			assertNull(zipInputStream.getNextEntry(), "zip should not contain entries");
		}
	}

	@Test
	void givenZipFileWhenEncryptAndArchiveZipThenEncryptsDeletesOriginalAndArchivesEncryptedFile(@TempDir Path sourceDir) throws Exception {
		// given
		Path zipFilePath = sourceDir.resolve("output.zip");
		Files.writeString(zipFilePath, "zip-content");

		long expectedZipFileSize = Files.size(zipFilePath);

		Path encryptedFilePath = sourceDir.resolve("output.zip" + AESUtils.CIPHER_EXTENSION);
		Files.writeString(encryptedFilePath, "encrypted-content");

		Path targetPath = sourceDir.resolve("archive");

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.encrypt(TEST_PASSWORD, zipFilePath.toFile()))
					.thenReturn(encryptedFilePath.toFile());

			// when
			Long result = service.encryptAndArchiveZip(zipFilePath, targetPath);

			// then
			assertEquals(expectedZipFileSize, result);

			assertFalse(Files.exists(zipFilePath), "original zip should be deleted");
			assertFalse(Files.exists(encryptedFilePath), "encrypted file should be deleted from source folder after archive");

			Path archivedEncryptedFile = targetPath.resolve(encryptedFilePath.getFileName());
			assertTrue(Files.exists(archivedEncryptedFile), "encrypted file should be archived");
			assertEquals("encrypted-content", Files.readString(archivedEncryptedFile));

			mockedAESUtils.verify(() -> AESUtils.encrypt(TEST_PASSWORD, zipFilePath.toFile()));
		}
	}

	@Test
	void givenEncryptionFailsWhenEncryptAndArchiveZipThenThrowsIllegalStateException(@TempDir Path sourceDir) throws Exception {
		// given
		Path zipFilePath = sourceDir.resolve("output.zip");
		Files.writeString(zipFilePath, "zip-content");

		Path targetPath = sourceDir.resolve("archive");

		try (MockedStatic<AESUtils> mockedAESUtils = mockStatic(AESUtils.class)) {
			mockedAESUtils.when(() -> AESUtils.encrypt(TEST_PASSWORD, zipFilePath.toFile()))
					.thenThrow(IllegalStateException.class);

			// when then
			assertThrows(
					IllegalStateException.class,
					() -> service.encryptAndArchiveZip(zipFilePath, targetPath)
			);

			assertTrue(Files.exists(zipFilePath), "zip should still exist if encryption fails");

			mockedAESUtils.verify(() -> AESUtils.encrypt(TEST_PASSWORD, zipFilePath.toFile()));
		}
	}
}
