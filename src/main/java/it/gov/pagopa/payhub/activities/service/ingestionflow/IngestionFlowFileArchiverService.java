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
	 * The password used for encrypting files.
	 */
	private final String dataCipherPsw;

	private final ZipFileService zipFileService;

	public IngestionFlowFileArchiverService(@Value("${data-cipher.encrypt-psw:psw}") String dataCipherPsw,
	                                        ZipFileService zipFileService) {
		this.dataCipherPsw = dataCipherPsw;
		this.zipFileService = zipFileService;
	}

	/**
	 * Compresses the specified list of files into a single archive, encrypts the resulting file, and
	 * moves it to the target directory. Intermediate files created during the process are cleaned up.
	 *
	 * @param files2Archive the list of files to be compressed and encrypted.
	 * @param file2Zip the path of the temporary output file used for compression.
	 * @param targetPath the destination path where the encrypted archive will be saved.
	 * @throws IOException if an error occurs during compression, encryption, file copying, or cleanup.
	 */
	public void compressAndArchive(List<Path> files2Archive, Path file2Zip, Path targetPath) throws IOException {
		File zipped = zipFileService.zipper(file2Zip, files2Archive);
		File encrypted = AESUtils.encrypt(dataCipherPsw, zipped);
		Files.delete(zipped.toPath());
		archive(List.of(encrypted.toPath()), targetPath);
	}

	/**
	 * Moves the specified list of files to the target directory and removes the original files after the move.
	 * Creates the target directory if it does not exist.
	 *
	 * @param files2Archive the list of files to move to the target directory.
	 * @param targetPath the directory where the files will be moved.
	 * @throws IOException if an error occurs during directory creation, file movement, or cleanup.
	 */
	public void archive(List<Path> files2Archive, Path targetPath) throws IOException {
		Files.createDirectories(targetPath);
		for (Path file : files2Archive) {
			Files.copy(file, targetPath, REPLACE_EXISTING);
			Files.deleteIfExists(file);
		}
	}
}

