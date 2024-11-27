package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class IngestionFileHandlerService {
	private static final String TEMPORARY_PATH = "TEMP";

	private final String dataCipherPsw;

	public IngestionFileHandlerService(@Value("${data-cipher.encrypt-psw}") String dataCipherPsw) {
		this.dataCipherPsw = dataCipherPsw;
	}

	public Path setUpProcess(String relativePath, String filename) throws IOException {
		Path relativePathDir = Paths.get(relativePath);
		Path encryptedFilePath = relativePathDir.resolve(filename);

		Path temporaryPath = relativePathDir.resolve(TEMPORARY_PATH);
		Files.createDirectories(temporaryPath);

		String filenameNoCipher = filename.replace(AESUtils.CIPHER_EXTENSION, "");
		Path temporaryZipFilePath = temporaryPath.resolve(filenameNoCipher);

		log.debug("Decrypting file: {}", encryptedFilePath);
		AESUtils.decrypt(dataCipherPsw, encryptedFilePath.toFile(), temporaryZipFilePath.toFile());

		log.debug("Validating ZIP file: {}", temporaryZipFilePath);
		FileUtils.isArchive(temporaryZipFilePath);

		String unzippedFilename = filenameNoCipher.replace(".zip", ".xml");
		Path outputUnzippedPath = temporaryPath.resolve(unzippedFilename);

		log.debug("Unzipping file: {} to {}", temporaryZipFilePath, outputUnzippedPath);
		FileUtils.unzip(temporaryZipFilePath, outputUnzippedPath);

		log.debug("File setup process completed successfully for: {}", filename);
		return outputUnzippedPath;
	}
}
