package it.gov.pagopa.payhub.activities.service;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import it.gov.pagopa.payhub.activities.dto.ingestion.CsvReadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.BiConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

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
     * @param header      The header of the CSV, as a list of String[].
     * @param data        The data to write to the CSV, as a list of String[].
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
        log.info("CSV file created successfully: {}", csvFilePath);
    }

    private ICSVWriter buildCsvWriter(File file) throws IOException {
        return new CSVWriterBuilder(new FileWriter(file))
                .withSeparator(separator)
                .withQuoteChar(quoteChar)
                .build();
    }

    /**
     * Reads a CSV file and converts it into a Stream of objects of a specified generic type.
     *
     * <p>To optimize memory usage in batch processing, this method uses {@code CsvToBean.iterator()}
     * instead of {@code parse()} or {@code stream()}. This approach reads and processes one record at a time,
     * reducing memory consumption at the cost of lower performance.</p>
     *
     * @param <T>                        The generic type of the DTO to which CSV rows will be mapped.
     * @param csvFilePath                 The path to the CSV file to read.
     * @param typeClass                   The class type to map each row of the CSV to.
     * @param ingestionFlowFileLineNumber  A {@link BiConsumer} used to assign a line number to each record.
     * @return A {@link CsvReadResult} containing a stream of parsed objects and the total number of rows.
     * @throws IOException If an error occurs while reading the file or parsing its contents.
     */
    public <T> Iterator<T> readCsv(Path csvFilePath, Class<T> typeClass, BiConsumer<T, Long> ingestionFlowFileLineNumber) throws IOException {
        FileReader fileReader = new FileReader(csvFilePath.toFile());

        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(typeClass);

        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(fileReader)
                .withType(typeClass)
                .withMappingStrategy(strategy)
                .withSeparator(separator)
                .withQuoteChar(quoteChar)
                .withIgnoreLeadingWhiteSpace(true)
                .withThrowExceptions(true)
                .build();

        log.info("CSV file read successfully: {}", csvFilePath);

        AtomicLong rowNumber = new AtomicLong(0);
        Iterator<T> iterator = csvToBean.iterator();

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T fileLineNumber = iterator.next();
                ingestionFlowFileLineNumber.accept(fileLineNumber, rowNumber.incrementAndGet());
                return fileLineNumber;
            }
        };
    }
}
