package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class IngestionFlowFileAchiverService {
	/**
	 * The target directory used for storing processed files.
	 */
	private final String storeDirectory;

	/**
	 * The password used for ecrypting files.
	 */
	private final String dataCipherPsw;

	private final ZipFileService zipFileService;

	public IngestionFlowFileAchiverService(@Value("${stored-dir:/processed/}") String storeDirectory,
	                                       @Value("${data-cipher.encrypt-psw:psw}") String dataCipherPsw,
	                                       ZipFileService zipFileService) {
		this.storeDirectory = storeDirectory;
		this.dataCipherPsw = dataCipherPsw;
		this.zipFileService = zipFileService;
	}

	/**
	 * Compresses a list of files into a ZIP archive, encrypts the archive, moves it to a target directory,
	 * and cleans up the working directory by deleting all files and subdirectories.
	 *
	 * @param files         the list of {@link Path} objects representing the files to be compressed.
	 *                      The files must reside in the same working directory.
	 * @param sourcePath    the source directory path used to resolve the target directory for the archive.
	 * @param outputFilename the name of the output ZIP file.
	 * @throws IOException if an I/O error occurs during file operations such as compression, moving, or deletion.
	 */
	public void compressArchiveFileCleanUp(List<Path> files, Path sourcePath, String outputFilename) throws IOException {
		Path workingPath = files.get(0).getParent();
		Path zipFilePath = workingPath.resolve(outputFilename);
		File zipped = zipFileService.zipper(zipFilePath, files);

		AESUtils.encrypt(dataCipherPsw, zipped);

		Path targetPath = sourcePath.resolve(storeDirectory);

		zipFileService.moveFile(zipped, targetPath);

		Files.walk(workingPath)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
	}
}
