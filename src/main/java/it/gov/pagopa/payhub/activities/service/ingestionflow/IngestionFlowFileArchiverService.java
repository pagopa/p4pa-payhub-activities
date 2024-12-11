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
public class IngestionFlowFileArchiverService {
	/**
	 * The target directory used for storing processed files.
	 */
	private final String targetDirectory;

	/**
	 * The password used for encrypting files.
	 */
	private final String dataCipherPsw;

	private final ZipFileService zipFileService;

	public IngestionFlowFileArchiverService(@Value("${stored-dir:/processed/}") String targetDirectory,
	                                        @Value("${data-cipher.encrypt-psw:psw}") String dataCipherPsw,
	                                        ZipFileService zipFileService) {
		this.targetDirectory = targetDirectory;
		this.dataCipherPsw = dataCipherPsw;
		this.zipFileService = zipFileService;
	}

	/**
	 * Compresses the given list of files into a single archive and encrypts the result.
	 *
	 * @param files      the list of files to be compressed.
	 * @param outputFile the path of the output compressed and encrypted file.
	 * @return the encrypted file resulting from the compression and encryption process.
	 */
	public File compressAndArchive(List<Path> files, Path outputFile) {
		File zipped = zipFileService.zipper(outputFile, files);
		return AESUtils.encrypt(dataCipherPsw, zipped);
	}

	/**
	 * Moves the specified file to a target directory and deletes additional specified files.
	 *
	 * @param fileLocation  the path of the file to move to the target directory.
	 * @param workingPathsToDelete additional files to be deleted after the move.
	 * @throws IOException if an I/O error occurs during file operations.
	 */
	public void moveToTargetAndCleanUp(Path fileLocation, Path... workingPathsToDelete) throws IOException {
		Path target = fileLocation.getParent().resolve(targetDirectory);
		Files.createDirectories(target);
		Files.copy(fileLocation, target.resolve(fileLocation.toFile().getName()), REPLACE_EXISTING);
		Files.delete(fileLocation);
		for (Path file : workingPathsToDelete) {
			Files.deleteIfExists(file);
		}
	}
}

