package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.util.FilesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ZipFileServiceTest {
	private static final int MAX_ENTRIES = 1000;
	private static final long MAX_UNCOMPRESSED_SIZE = 50 * 1024 * 1024L;
	private static final double MAX_COMPRESSION_RATIO = 0.1;

	@TempDir
	private Path tempDir;

	private Path zipFile;
	private ZipFileService service;

	@BeforeEach
	void setup() throws IOException {
		service = new ZipFileService(MAX_ENTRIES, MAX_UNCOMPRESSED_SIZE, MAX_COMPRESSION_RATIO);
		zipFile = tempDir.resolve("test.zip");
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			FilesUtils.addZipEntry(zos, "file1.txt", "This is the content of file1.");
			FilesUtils.addZipEntry(zos, "file2.txt", "This is the content of file2.");
		}
	}

	@Test
	void testUnzipValidArchive() throws IOException {
		Path outputDir = tempDir.resolve("output");
		System.out.println(outputDir);
		assertDoesNotThrow(() -> service.unzip(zipFile, outputDir));
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
				FilesUtils.addZipEntry(zos, "file" + i + ".txt", "Content of file " + i);
			}
		}

		Path outputDir = tempDir.resolve("output");
		assertThrows(InvalidIngestionFileException.class,
			() -> service.unzip(zipFile, outputDir), "exceeds the maximum number of entries"
		);
	}

	@Test
	void testUnzipWithMockedFileSystem() {
		Path mockZip = mock(Path.class);
		when(mockZip.toFile()).thenReturn(zipFile.toFile());

		Path outputDir = tempDir.resolve("output");

		assertDoesNotThrow(() -> service.unzip(mockZip, outputDir));
		verify(mockZip, atLeastOnce()).toFile();
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
			() -> service.unzip(zipFile, outputDir), "Bad zip entry"
		);
	}

	@Test
	void testUnzipWithZeroCompressedSize() throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			ZipEntry zeroSizeEntry = new ZipEntry("empty.txt");
			zos.putNextEntry(zeroSizeEntry);
			zos.closeEntry();
		}

		Path outputDir = tempDir.resolve("output");
		assertDoesNotThrow(() -> service.unzip(zipFile, outputDir));

		assertTrue(Files.exists(outputDir.resolve("empty.txt")), "Expected empty.txt to exist");
	}


	@Test
	void givenValidFileNameThenOk() throws IllegalArgumentException {
		String validFileName = "safeFile.txt";
		assertDoesNotThrow(() -> service.checkFileName(validFileName));
	}

	@Test
	void givenInvalidFileNameStartingWithNonAlphanumericThenException() {
		String invalidFileName = "/unsafeFile.txt";
		assertThrows(InvalidIngestionFileException.class, () -> service.checkFileName(invalidFileName));
	}

	@Test
	void givenInvalidFileNameContainingDotDotThenException() {
		String invalidFileName = "safe/../../unsafeFile.txt";
		assertThrows(InvalidIngestionFileException.class, () -> service.checkFileName(invalidFileName));
	}

	@Test
	void givenValidZipEntryThenReturnZipEntry() throws IllegalArgumentException {
		ZipEntry validEntry = new ZipEntry("safeFile.txt");
		assertEquals(validEntry, service.checkFileName(validEntry));
	}
}
