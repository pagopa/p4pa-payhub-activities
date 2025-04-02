package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service class responsible for validation files.
 */
@Lazy
@Slf4j
@Service
public class FileValidatorService {

	/**
	 * Validates that the file exists and is a regular file.
	 *
	 * @param filePath the path to the file.
	 * @throws InvalidIngestionFileException if the file does not exist or is not a regular file.
	 */
	public void validateFile(Path filePath) {
		if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
			throw new InvalidIngestionFileException("File not found: " + filePath);
		}
	}

	/**
	 * Checks if the specified file is a valid archive by analyzing its signature.
	 *
	 * @param zipFilePath the path to the ZIP file to check.
	 * @return true if the file is a valid ZIP archive; false otherwise.
	 * @throws InvalidIngestionFileException if the file is not a valid archive.
	 */
	public boolean isArchive(Path zipFilePath) {
		try (RandomAccessFile raf = new RandomAccessFile(zipFilePath.toFile(), "r")) {
			int fileSignature = raf.readInt();
			return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Invalid zip file");
		}
	}

	/**
	 * Checks if the specified file is a valid ZIP file by checking file extension.
	 *
	 * @param zipFilePath the path to the ZIP file to check.
	 * @return true if the file is a valid ZIP archive; false otherwise.
	 */
	public boolean isZipFileByExtension(Path zipFilePath) {
		return zipFilePath.getFileName().toString().toLowerCase().endsWith(".zip");
	}
}
