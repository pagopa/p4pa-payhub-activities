package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IngestionFileValidatorServiceTest {

	private IngestionFileValidatorService validatorService;

	@BeforeEach
	void setUp() {
		validatorService = new IngestionFileValidatorService();
	}

	@Test
	void validate_ValidFile_Success() throws IOException, NoSuchAlgorithmException {
		// Given
		Path tempDir = Files.createTempDirectory("testDir");
		Path filePath = tempDir.resolve("test.zip");
		Path md5Path = tempDir.resolve("test.md5");
		Path authPath = tempDir.resolve("test.auth");
		String requestTokenCode = "valid-token";

		Files.writeString(filePath, "Test content");
		Files.writeString(authPath, requestTokenCode);

		MessageDigest md = MessageDigest.getInstance("MD5");
		try (InputStream is = Files.newInputStream(filePath);
		     DigestInputStream dis = new DigestInputStream(is, md)) {
			dis.readAllBytes();
		}
		String calculatedMd5 = Hex.encodeHexString(md.digest());
		Files.writeString(md5Path, calculatedMd5);

		// When Then
		assertDoesNotThrow(() -> validatorService.validate(tempDir.toString(), "test.zip", requestTokenCode));

		// Cleanup
		Files.deleteIfExists(filePath);
		Files.deleteIfExists(md5Path);
		Files.deleteIfExists(authPath);
		Files.deleteIfExists(tempDir);
	}

	@Test
	void validate_InvalidMD5_ThrowsException() throws IOException {
		// Given
		Path tempDir = Files.createTempDirectory("testDir");
		Path filePath = tempDir.resolve("test.zip");
		Path md5Path = tempDir.resolve("test.md5");
		Path authPath = tempDir.resolve("test.auth");
		String requestTokenCode = "valid-token";

		Files.writeString(filePath, "Test content");
		Files.writeString(md5Path, "wrong-md5");
		Files.writeString(authPath, requestTokenCode);

		// When Then
		assertThrows(InvalidIngestionFileException.class,
			() -> validatorService.validate(tempDir.toString(), "test.zip", requestTokenCode)
		);
		// Cleanup
		Files.deleteIfExists(filePath);
		Files.deleteIfExists(md5Path);
		Files.deleteIfExists(authPath);
		Files.deleteIfExists(tempDir);
	}

	@Test
	void validate_InvalidAuth_ThrowsException() throws IOException {
		// Given
		Path tempDir = Files.createTempDirectory("testDir");
		Path filePath = tempDir.resolve("test.zip");
		Path md5Path = tempDir.resolve("test.md5");
		Path authPath = tempDir.resolve("test.auth");
		String requestTokenCode = "valid-token";
		
		Files.writeString(filePath, "Test content");
		Files.writeString(md5Path, "dummy-md5");
		Files.writeString(authPath, "invalid-token");

		// When Then
		assertThrows(InvalidIngestionFileException.class,
			() -> validatorService.validate(tempDir.toString(), "test.zip", requestTokenCode)
		);

		// Cleanup
		Files.deleteIfExists(filePath);
		Files.deleteIfExists(md5Path);
		Files.deleteIfExists(authPath);
		Files.deleteIfExists(tempDir);
	}

	@Test
	void validate_FileNotFound_ThrowsException() {
		// Given
		String tempDir = "non-existent-dir";
		String filename = "non-existent.zip";

		// When Then
		assertThrows(InvalidIngestionFileException.class,
			() -> validatorService.validate(tempDir.toString(), filename, "valid-token")
		);
	}
}
