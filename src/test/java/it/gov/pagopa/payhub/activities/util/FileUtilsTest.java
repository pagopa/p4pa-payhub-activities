package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {

	private Path zipPath;
	private Path outputDir;

	@BeforeEach
	public void setUp() throws Exception {
		zipPath = Files.createTempFile("testZip", ".zip");
		outputDir = Files.createTempDirectory("output");

		// Crea un file ZIP di test
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
			ZipEntry entry = new ZipEntry("testFile.xml");
			zos.putNextEntry(entry);
			zos.write("This is a test file".getBytes());
			zos.closeEntry();
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		// Pulisce i file temporanei
		Files.deleteIfExists(zipPath);
		Files.walk(outputDir)
		  .map(Path::toFile)
		  .forEach(file -> file.delete());
	}

	@Test
	public void testValidateZip_doesNotThrowExceptionWhenValid(){
		assertDoesNotThrow(() -> FileUtils.validateZip(zipPath));
	}

	@Test
	public void testValidateZip_throwsExceptionWhenInvalid() throws Exception {

		Path invalidZipPath = Files.createTempFile("invalidZip", ".zip");

		assertThrows(InvalidIngestionFileException.class, () -> FileUtils.validateZip(invalidZipPath));

		Files.deleteIfExists(invalidZipPath);
	}

	@Test
	public void testUnzipFile_extractsFileSuccessfully() {
		Path result = FileUtils.unzipFile(zipPath, outputDir);

		Path expectedFilePath = outputDir.resolve("testFile.xml");
		assertEquals(expectedFilePath, result, "Returned path should match the extracted file path");
	}
}
