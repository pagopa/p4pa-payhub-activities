package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Service class responsible for handling archiving ingestion files,
 * including zipping and ecryption files.
 */
@Lazy
@Service
public class IngestionFlowFileAchiverService {
	/**
	 * The target directory used for storing processed files.
	 */
	private final String targetDirectory;

	/**
	 * The password used for encrypting files.
	 */
	private final String dataCipherPsw;

	private final ZipFileService zipFileService;

	public IngestionFlowFileAchiverService(@Value("${stored-dir:/processed/}") String targetDirectory,
	                                       @Value("${data-cipher.encrypt-psw:psw}") String dataCipherPsw,
	                                       ZipFileService zipFileService) {
		this.targetDirectory = targetDirectory;
		this.dataCipherPsw = dataCipherPsw;
		this.zipFileService = zipFileService;
	}

	/**
	 * Compresses a list of files into a ZIP archive, encrypts the archive, moves it to a target directory,
	 * and cleans up the working directory.
	 *
	 * @param files the list of files to compress.
	 * @param sourcePath the source directory path.
	 * @param outputFilename the name of the output ZIP file.
	 * @throws IOException if an I/O error occurs during compression, encryption, moving, or cleanup.
	 */
	public void compressArchiveFileAndCleanUp(List<Path> files, String sourcePath, String outputFilename) throws IOException {
		Path zipFilePath = Path.of(sourcePath, outputFilename + ".zip");
		File zipped = zipFileService.zipper(zipFilePath, files);

		File encryptedFile = AESUtils.encrypt(dataCipherPsw, zipped);

		Path targetPath = Path.of(targetDirectory, encryptedFile.getName());
		Files.createDirectories(targetPath.getParent());
		Files.move(encryptedFile.toPath(), targetPath, REPLACE_EXISTING);

		for (Path file : files) {
			Files.deleteIfExists(file);
		}
		Files.deleteIfExists(zipped.toPath());
	}
}

