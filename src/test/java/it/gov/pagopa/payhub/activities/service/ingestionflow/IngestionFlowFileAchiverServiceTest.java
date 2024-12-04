package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class IngestionFlowFileAchiverServiceTest {
	private static final String TEST_CIPHER_PSW = "testPassword";
	private static final String STORE_PATH = "/processed/";
	private static final String TEMPORARY_PATH = "/tmp/";

	private ZipFileService zipFileService;
	private IngestionFlowFileAchiverService service;

	@TempDir
	private Path tempDir;

	@BeforeEach
	void setup() {
		zipFileService = new ZipFileService(1000, 50 * 1024 * 1024L, 0.1);
		service = new IngestionFlowFileAchiverService(STORE_PATH, TEST_CIPHER_PSW, zipFileService);
	}

	@Test
	void testCompressAndArchiveFile() throws IOException {
		Path workingPath = tempDir.resolve(TEMPORARY_PATH);
		Files.createDirectories(workingPath);
		Path targetDir = tempDir.resolve(STORE_PATH);
		Files.createDirectories(targetDir);

		Path testFile1 = Files.createFile(workingPath.resolve("test_1.txt"));
		Path testFile2 = Files.createFile(workingPath.resolve("test_2.txt"));
		List<Path> files = List.of(testFile1, testFile2);

		Path zipFilePath = workingPath.resolve("output.zip");

		try (var aesUtilsMocked = mockStatic(AESUtils.class)) {
			File zippedFile = zipFileService.zipper(zipFilePath, files);

			aesUtilsMocked.when(() -> AESUtils.encrypt(TEST_CIPHER_PSW, zippedFile)).then(invocation -> null);
			AESUtils.encrypt(TEST_CIPHER_PSW, zippedFile);

			zipFileService.moveFile(zippedFile, targetDir);

			Path movedFile = targetDir.resolve("output.zip");
			assertTrue(Files.exists(movedFile), "The file should have been moved to the target directory");
			aesUtilsMocked.verify(() -> AESUtils.encrypt(TEST_CIPHER_PSW, zippedFile), times(1));
		}
		Files.walk(workingPath)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
		assertFalse(Files.exists(workingPath), "The workingPath should have been removed");
	}
}