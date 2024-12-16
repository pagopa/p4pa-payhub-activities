package it.gov.pagopa.payhub.activities.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvUtils {

    private CsvUtils() {}

    /**
     * Creates a CSV file from the provided header and data.
     *
     * @param filePath The full path where the CSV file should be saved.
     * @param header   The header of the CSV, as a list of String[].
     * @param data     The data to write to the CSV, as a list of String[].
     * @throws IOException If an error occurs while writing the file.
     */
    public static void createCsv(String filePath, List<String[]> header, List<String[]> data) throws IOException {
        // Create the destination folder if it doesn't already exist
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Unable to create directory: " + parentDir.getAbsolutePath());
        }

        // Create the CSV file
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
            // Write the header
            if (header != null && !header.isEmpty()) {
                csvWriter.writeAll(header);
            }

            // Write the data
            if (data != null && !data.isEmpty()) {
                csvWriter.writeAll(data);
            }
        }
        log.info("CSV file created successfully: " + filePath);
    }
}
