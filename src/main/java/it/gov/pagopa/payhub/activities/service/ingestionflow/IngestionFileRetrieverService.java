package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.service.FileValidatorService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.service.ZipFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service class responsible for handling encrypted ingestion files,
 * including decryption, validation, and extraction of ZIP files.
 */
@Lazy
@Slf4j
@Service
public class IngestionFileRetrieverService {

	/**
	 * The temporary directory used for working process.
	 */
	private static final Path TEMPORARY_PATH = Path.of("/tmp/");

	/**
	 * The password used for decrypting encrypted files.
	 */
	private final String dataCipherPsw;

	private final FileValidatorService fileValidatorService;
	private final ZipFileService zipFileService;

	public IngestionFileRetrieverService(@Value("${data-cipher.encrypt-psw:psw}") String dataCipherPsw,
	                                     FileValidatorService fileValidatorService, ZipFileService zipFileService) {
		this.dataCipherPsw = dataCipherPsw;
		this.fileValidatorService = fileValidatorService;
		this.zipFileService = zipFileService;
	}

	/**
	 * Handles the setup process for an ingestion file by performing the following steps:
	 * <ul>
	 *     <li>Decrypts the file using AES encryption.</li>
	 *     <li>Validates if the file is a valid ZIP archive.</li>
	 *     <li>Extracts the contents of the ZIP file to a temporary directory.</li>
	 * </ul>
	 *
	 * @param sourcePath the relative path to the directory containing the file.
	 * @param filename the name of the file to process.
	 * @return the path to the extracted file.
	 * @throws IOException if any file operation fails during the setup process.
	 */
	public List<Path> retrieveFile(Path sourcePath, String filename) throws IOException {
		Path encryptedFilePath = sourcePath.resolve(filename);
		fileValidatorService.validateFile(encryptedFilePath);

		Path workingPath = TEMPORARY_PATH.resolve(sourcePath.subpath(0, sourcePath.getNameCount()));
		Files.createDirectories(workingPath);

		String filenameNoCipher = filename.replace(AESUtils.CIPHER_EXTENSION, "");
		Path zipFilePath = workingPath.resolve(filenameNoCipher);

		log.debug("Decrypting file: {}", encryptedFilePath);
		AESUtils.decrypt(dataCipherPsw,
			encryptedFilePath.toFile(),
			workingPath.resolve(Paths.get(filenameNoCipher)).toFile());

		log.debug("Validating ZIP file: {}", zipFilePath);
		fileValidatorService.isArchive(zipFilePath);

		log.debug("Unzipping files in : {}", workingPath);
		List<Path> unzippedPaths = zipFileService.unzip(workingPath);

		log.debug("File process completed successfully for: {}", filenameNoCipher);
		return unzippedPaths;
	}
}
