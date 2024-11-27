package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

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

@Slf4j
@Service
public class IngestionFileValidatorService {

	public void validate(String relativePath, String filename, String requestTokenCode) {
		Path fileLocation = Paths.get(relativePath, filename);

		validateFile(fileLocation);
		validateAUTH(fileLocation.toString(), requestTokenCode);
		validateMD5(fileLocation.toString());
	}

	private void validateFile(Path filePath) {
		if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
			throw new InvalidIngestionFileException("File non trovato: " + filePath);
		}
	}

	private void validateMD5(String filename) {
		String md5 = filename.replace(".zip", ".md5");
		String valueMD5 = readContentFile(md5);
		String calculatedMD5 = calculateMd5(filename);
		if(!valueMD5.equalsIgnoreCase(calculatedMD5)) {
			throw new InvalidIngestionFileException("Error while calculating MD5 file value");
		}
	}

	private void validateAUTH(String filename, String requestToken) {
		String auth = filename.replace(".zip", ".auth");
		String valueAUTH = readContentFile(auth);
		if(!valueAUTH.equalsIgnoreCase(requestToken)) {
			throw new InvalidIngestionFileException("Error while calculating MD5 file value");
		}
	}

	private String readContentFile(String filename) {
		try {
			return Files.readString(Paths.get(filename));
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error while reading file: " + filename);
		}
	}

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
