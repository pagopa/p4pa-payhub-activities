package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

	private FileUtils() {
	}

	public static boolean isArchive(Path zipFilePath) {
		try (RandomAccessFile raf = new RandomAccessFile(zipFilePath.toFile(), "r")) {
			int fileSignature = raf.readInt();
			return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Invalid zip file");
		}
	}

	public static void unzip(Path source, Path target) {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
			ZipEntry zipEntry = zis.getNextEntry();
			SecureFileUtils.checkFileName(zipEntry);
			while (zipEntry != null) {
				if (zipEntry.isDirectory()) {
					throw new InvalidIngestionFileException("ZIP file contains directories, but only files are expected");
				}
				Path targetPath = zipSlipProtect(zipEntry, target);
				Files.createDirectories(targetPath);
				Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
		} catch (IOException e) {
			throw new InvalidIngestionFileException("Error while unzipping file: " + source);
		}
	}

	public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) {
		String checkedFilename = SecureFileUtils.checkFileName(zipEntry.getName());
		Path targetDirResolved = targetDir.resolve(checkedFilename);
		Path normalizePath = targetDirResolved.normalize();
		if (!normalizePath.startsWith(targetDir)) {
			throw new InvalidIngestionFileException("Bad zip entry: " + zipEntry.getName());
		}
		return normalizePath;
	}
}
