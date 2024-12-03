package it.gov.pagopa.payhub.activities.util;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FilesUtils {

	// Helper method to add entries to the ZIP file
	public static void addZipEntry(ZipOutputStream zos, String entryName, String content) throws IOException {
		zos.putNextEntry(new ZipEntry(entryName));
		zos.write(content.getBytes());
		zos.closeEntry();
	}
}
