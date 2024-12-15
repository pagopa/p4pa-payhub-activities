package it.gov.pagopa.payhub.activities.util;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvUtilsTest {

    @Test
    void testCreateCsv_success() throws IOException {
        // Give
        String filePath = "test/output.csv";

        String[] headerArray= new String[]{"Header1", "Header2"};
        List<String[]> header = new ArrayList<>(List.of());
        header.add(headerArray);
        List<String[]> data = Arrays.asList(new String[]{"Data1", "Data2"}, new String[]{"Data3", "Data4"});

        // When
        CsvUtils.createCsv(filePath, header, data);

        // Then
        File file = new File(filePath);
        assertTrue(file.exists(), "The file should exist.");
        assertTrue(file.length() > 0, "The file should not be empty.");
    }

    @Test
    void testCreateCsv_invalidDirectory() {
        // Give
        String filePath = "D:\\\\dummy.txt";
        String[] headerArray= new String[]{"Header1", "Header2"};
        List<String[]> header = new ArrayList<>(List.of());
        header.add(headerArray);
        List<String[]> data = Arrays.asList(new String[]{"Data1", "Data2"}, new String[]{"Data3", "Data4"});

        // When
        IOException exception = assertThrows(IOException.class, () -> {
            CsvUtils.createCsv(filePath, header, data);
        });

        // Then
        assertTrue(exception.getMessage().contains("Unable to create directory"));
    }

    @Test
    void testCreateCsv_noData() throws IOException {
        // Give
        String filePath = "test/empty.csv";
        String[] headerArray= new String[]{"Header1", "Header2"};
        List<String[]> header = new ArrayList<>(List.of());
        header.add(headerArray);
        List<String[]> data = List.of();

        // When
        CsvUtils.createCsv(filePath, header, data);

        // Then
        File file = new File(filePath);
        assertTrue(file.exists(), "The file should exist.");
        assertTrue(file.length() > 0, "The file should not be empty.");
    }

    @Test
    void testCreateCsv_noHeader() throws IOException {
        // Give
        String filePath = "test/no_header.csv";
        List<String[]> header = List.of();
        List<String[]> data = Arrays.asList(new String[]{"Data1", "Data2"}, new String[]{"Data3", "Data4"});

        // When
        CsvUtils.createCsv(filePath, header, data);

        // Then
        File file = new File(filePath);
        assertTrue(file.exists(), "The file should exist.");
        assertTrue(file.length() > 0, "The file should not be empty.");
    }
}
