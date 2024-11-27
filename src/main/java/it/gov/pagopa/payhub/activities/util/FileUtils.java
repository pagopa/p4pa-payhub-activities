package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

	private FileUtils() {
	}

	public static void validateZip(Path zipFilePath) {
		try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
			zipFile.entries();
		} catch (Exception e) {
			throw new InvalidIngestionFileException("Invalid zip file");
		}
	}

	public static Path unzipFile(Path zipFilePath, Path outputDir) {
		Path extractedFilePath;
		try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			if (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					throw new InvalidIngestionFileException("ZIP file contains directories, but only files are expected");
				}
				String checkedFilename = SecureFileUtils.checkFileName(entry.getName());
				Path entryPath = outputDir.resolve(checkedFilename);
				Files.createDirectories(entryPath.getParent());
				try {
					Files.copy(zipFile.getInputStream(entry), entryPath, StandardCopyOption.REPLACE_EXISTING);
					extractedFilePath = entryPath;  // Restituisci il path completo del file estratto
				} catch (IOException e) {
					throw new InvalidIngestionFileException("Failed to extract file: " + entry.getName());
				}
			} else {
				throw new InvalidIngestionFileException("ZIP file is empty or contains no files");
			}
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error while unzipping file: " + zipFilePath);
		}
		return extractedFilePath;
	}
}
