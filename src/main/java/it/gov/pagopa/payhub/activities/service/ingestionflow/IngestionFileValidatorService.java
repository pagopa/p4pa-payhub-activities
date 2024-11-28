package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service class for validating ingestion files.
 * Provides methods to validate the existence, integrity, and authenticity of files.
 */
@Slf4j
@Service
public class IngestionFileValidatorService {

	/**
	 * Validates the ingestion file based on its existence, MD5 checksum, and authentication token.
	 *
	 * @param relativePath the relative path to the directory containing the file.
	 * @param filename the name of the file to validate.
	 * @param requestTokenCode the expected authentication token for the file.
	 * @throws InvalidIngestionFileException if the file fails any validation step.
	 */
	public void validate(String relativePath, String filename, String requestTokenCode) {
		Path fileLocation = Paths.get(relativePath, filename);

		validateFile(fileLocation);
		validateAUTH(fileLocation.toString(), requestTokenCode);
		validateMD5(fileLocation.toString());
	}

	/**
	 * Validates that the file exists and is a regular file.
	 *
	 * @param filePath the path to the file.
	 * @throws InvalidIngestionFileException if the file does not exist or is not a regular file.
	 */
	private void validateFile(Path filePath) {
		if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
			throw new InvalidIngestionFileException("File non trovato: " + filePath);
		}
	}

	/**
	 * Validates the MD5 checksum of the file by comparing it with the expected value stored in a `.md5` file.
	 *
	 * @param filename the name of the file to validate.
	 * @throws InvalidIngestionFileException if the calculated MD5 checksum does not match the expected value.
	 */
	private void validateMD5(String filename) {
		String md5 = filename.replace(".zip", ".md5");
		String valueMD5 = readContentFile(md5);
		String calculatedMD5 = calculateMd5(filename);
		if(!valueMD5.equalsIgnoreCase(calculatedMD5)) {
			throw new InvalidIngestionFileException("Error while calculating MD5 file value");
		}
	}

	/**
	 * Validates the authentication token of the file by comparing it with the expected value stored in a `.auth` file.
	 *
	 * @param filename the name of the file to validate.
	 * @param requestToken the expected authentication token.
	 * @throws InvalidIngestionFileException if the authentication token does not match the expected value.
	 */
	private void validateAUTH(String filename, String requestToken) {
		String auth = filename.replace(".zip", ".auth");
		String valueAUTH = readContentFile(auth);
		if(!valueAUTH.equalsIgnoreCase(requestToken)) {
			throw new InvalidIngestionFileException("Error while calculating MD5 file value");
		}
	}

	/**
	 * Reads the content of a file into a string.
	 *
	 * @param filename the name of the file to read.
	 * @return the content of the file as a string.
	 * @throws InvalidIngestionFileException if the file cannot be read.
	 */
	private String readContentFile(String filename) {
		try {
			return Files.readString(Paths.get(filename));
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error while reading file: " + filename);
		}
	}

	/**
	 * Calculates the MD5 checksum of a file.
	 *
	 * @param filename the name of the file.
	 * @return the calculated MD5 checksum as a hexadecimal string.
	 * @throws InvalidIngestionFileException if the checksum calculation fails.
	 */
	private String calculateMd5(String filename) {
		try (InputStream is = Files.newInputStream(Paths.get(filename))) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			DigestInputStream dis = new DigestInputStream(is, md);
			IOUtils.toByteArray(dis);
			return Hex.encodeHexString(md.digest());
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new InvalidIngestionFileException("Error while calculating MD5");
		}
	}
}
