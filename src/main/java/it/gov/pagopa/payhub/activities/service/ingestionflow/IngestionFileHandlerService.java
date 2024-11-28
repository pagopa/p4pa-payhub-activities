package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service class responsible for handling encrypted ingestion files,
 * including decryption, validation, and extraction of ZIP files.
 */
@Slf4j
@Service
public class IngestionFileHandlerService {
	private static final String TEMPORARY_PATH = "TEMP";

	/**
	 * The password used for decrypting encrypted files.
	 */
	private final String dataCipherPsw;

	/**
	 * Constructor for `IngestionFileHandlerService`.
	 *
	 * @param dataCipherPsw the password used for encryption and decryption,
	 *                      injected from application properties.
	 */
	public IngestionFileHandlerService(@Value("${data-cipher.encrypt-psw}") String dataCipherPsw) {
		this.dataCipherPsw = dataCipherPsw;
	}

	/**
	 * Handles the setup process for an ingestion file by performing the following steps:
	 * <ul>
	 *     <li>Decrypts the file using AES encryption.</li>
	 *     <li>Validates if the file is a valid ZIP archive.</li>
	 *     <li>Extracts the contents of the ZIP file to a temporary directory.</li>
	 * </ul>
	 *
	 * @param relativePath the relative path to the directory containing the file.
	 * @param filename the name of the file to process.
	 * @return the path to the extracted XML file.
	 * @throws IOException if any file operation fails during the setup process.
	 */
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
