package it.gov.pagopa.payhub.activities.util.csv;

import it.gov.pagopa.payhub.activities.service.CsvService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvServiceTest {

    private final CsvService csvService = new CsvService(';', '\"', "legacies");

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

    @Test
    void testReadCsv_success() throws IOException {
        // Given
        Path filePath = Path.of("build", "tmp", "test", "input.csv");
        List<String[]> data = Arrays.asList(
                new String[]{"Data1", "Data2", "2025-02-20"},
                new String[]{"Data4", "Data5", "2025-02-20"}
        );
        List<String> headers = List.of("Column1", "Column2", "Column3");
        List<String[]> headerList = new ArrayList<>();
        headerList.add(headers.toArray(new String[0]));

        csvService.createCsv(filePath, headerList, data);

        // When
        List<TestCsv> resultList = csvService.readCsv(filePath, TestCsv.class, iterator -> {
            List<TestCsv> list = new ArrayList<>();
            iterator.forEachRemaining(list::add);
            return list;
        });

        // Then
        List<String[]> actualData = resultList.stream()
                .map(testCsv -> new String[]{
                        testCsv.getColumn1(),
                        testCsv.getColumn2(),
                        String.valueOf(testCsv.getColumn3())
                })
                .toList();

        assertEquals(2, resultList.size());
        assertEquals(data.size(), actualData.size());
        assertArrayEquals(data.toArray(new String[0][]), actualData.toArray(new String[0][]));
    }

    @Test
    void testReadCsv_emptyFile() throws IOException {
        // Given
        Path filePath = Path.of("build", "tmp", "test", "empty.csv");
        List<String> headers = List.of("Column1", "Column2", "Column3");
        List<String[]> headerList = new ArrayList<>();
        headerList.add(headers.toArray(new String[0]));
        List<String[]> data = List.of();

        csvService.createCsv(filePath, headerList, data);

        // When
        List<TestCsv> resultList = csvService.readCsv(filePath, TestCsv.class, iterator -> {
            List<TestCsv> list = new ArrayList<>();
            iterator.forEachRemaining(list::add);
            return list;
        });

        // Then
        assertEquals(0, resultList.size());
    }

    @Test
    void testReadCsv_requiredColumn() throws IOException {
        // Given
        Path filePath = Path.of("build", "tmp", "test", "empty.csv");
        List<String> headers = List.of("Column1", "Column3");
        List<String[]> headerList = new ArrayList<>();
        headerList.add(headers.toArray(new String[0]));
        List<String[]> data = List.of();

        csvService.createCsv(filePath, headerList, data);

        // When & Then
        assertThrows(IOException.class, () ->
                csvService.readCsv(filePath, TestCsv.class, iterator -> {
                    List<TestCsv> list = new ArrayList<>();
                    iterator.forEachRemaining(list::add);
                    return list;
                })
        );
    }


    @Test
    void testReadCsv_invalidFile() {
        // Given
        Path filePath = Path.of("build", "tmp", "test", "nonexistent.csv");

        // When & Then
        assertThrows(IOException.class, () ->
                csvService.readCsv(filePath, TestCsv.class, iterator -> {
                    List<TestCsv> list = new ArrayList<>();
                    iterator.forEachRemaining(list::add);
                    return list;
                })
        );
    }

}
