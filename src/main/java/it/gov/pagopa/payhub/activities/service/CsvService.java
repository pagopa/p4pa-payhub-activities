package it.gov.pagopa.payhub.activities.service;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Lazy
@Service
@Slf4j
public class CsvService {

    private final char separator;
    private final char quoteChar;

    public CsvService(
            @Value("${csv.separator}") char separator,
            @Value("${csv.quote-char}") char quoteChar) {
        this.separator = separator;
        this.quoteChar = quoteChar;
    }

    /**
     * Creates a CSV file from the provided header and data.
     *
     * @param csvFilePath The full path where the CSV file should be saved.
     * @param header   The header of the CSV, as a list of String[].
     * @param data     The data to write to the CSV, as a list of String[].
     * @throws IOException If an error occurs while writing the file.
     */
    public void createCsv(Path csvFilePath, List<String[]> header, List<String[]> data) throws IOException {
        // Create the destination folder if it doesn't already exist
        File file = csvFilePath.toFile();
        File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Unable to create directory: " + parentDir.getAbsolutePath());
        }

        // Create the CSV file
        try (ICSVWriter csvWriter = buildCsvWriter(file)) {
            // Write the header
            if (header != null && !header.isEmpty()) {
                csvWriter.writeAll(header);
            }

            // Write the data
            if (data != null && !data.isEmpty()) {
                csvWriter.writeAll(data);
            }
        }
        log.info("CSV file created successfully: " + csvFilePath);
    }

    private ICSVWriter buildCsvWriter(File file) throws IOException {
        return new CSVWriterBuilder(new FileWriter(file))
                .withSeparator(separator)
                .withQuoteChar(quoteChar)
                .build();
    }
}
