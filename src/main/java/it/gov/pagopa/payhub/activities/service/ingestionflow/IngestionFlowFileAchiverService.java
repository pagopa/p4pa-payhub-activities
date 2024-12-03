package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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

	public void compressAndArchiveFile(List<Path> files, Path sourcePath, String outputFilename) throws IOException {
		Path workingPath = files.get(0).getParent();
		Path zipFilePath = workingPath.resolve(outputFilename);
		File zipped = zipFileService.zipper(zipFilePath, files);
		AESUtils.encrypt(dataCipherPsw, zipped);
		Path targetPath = sourcePath.resolve(storeDirectory);
		zipFileService.moveFile(zipped, targetPath);
	}
}
