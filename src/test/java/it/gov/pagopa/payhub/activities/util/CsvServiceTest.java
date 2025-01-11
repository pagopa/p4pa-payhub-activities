package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.service.CsvService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvServiceTest {

    private final CsvService csvService = new CsvService(';', '\"');

    @Test
    void testCreateCsv_success() throws IOException {
        // Give
        Path filePath = Path.of("build", "tmp", "test", "output.csv");

        String[] headerArray= new String[]{"Header1", "Header2"};
        List<String[]> header = new ArrayList<>(List.of());
        header.add(headerArray);
        List<String[]> data = Arrays.asList(new String[]{"Data1", "Data2"}, new String[]{"Data3", "Data4"});

        // When
        csvService.createCsv(filePath, header, data);

        // Then
        File file = filePath.toFile();
        assertTrue(file.exists(), "The file should exist.");
        assertTrue(file.length() > 0, "The file should not be empty.");
    }

    @Test
    void testCreateCsv_noData() throws IOException {
        // Give
        Path filePath = Path.of("build", "tmp", "test", "empty.csv");
        String[] headerArray= new String[]{"Header1", "Header2"};
        List<String[]> header = new ArrayList<>(List.of());
        header.add(headerArray);
        List<String[]> data = List.of();

        // When
        csvService.createCsv(filePath, header, data);

        // Then
        File file = filePath.toFile();
        assertTrue(file.exists(), "The file should exist.");
        assertTrue(file.length() > 0, "The file should not be empty.");
    }

    @Test
    void testCreateCsv_noHeader() throws IOException {
        // Give
        Path filePath = Path.of("build", "tmp", "test", "no_header.csv");
        List<String[]> header = List.of();
        List<String[]> data = Arrays.asList(new String[]{"Data1", "Data2"}, new String[]{"Data3", "Data4"});

        // When
        csvService.createCsv(filePath, header, data);

        // Then
        File file = filePath.toFile();
        assertTrue(file.exists(), "The file should exist.");
        assertTrue(file.length() > 0, "The file should not be empty.");
    }
}
