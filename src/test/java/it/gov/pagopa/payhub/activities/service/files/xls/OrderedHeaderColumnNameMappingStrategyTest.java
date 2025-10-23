package it.gov.pagopa.payhub.activities.service.files.xls;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class OrderedHeaderColumnNameMappingStrategyTest {
    static class PersonDto {
        @CsvBindByName(column = "first_name")
        private String firstName;

        @CsvBindByName(column = "last_name")
        private String lastName;

        @CsvBindByName(column = "age")
        private int age;
    }

    @Test
    void testGenerateHeaderWithBean() throws Exception {
        OrderedHeaderColumnNameMappingStrategy<PersonDto> strategy = new OrderedHeaderColumnNameMappingStrategy<>() {
        };
        strategy.setType(PersonDto.class);

        String[] headers = strategy.generateHeader(new PersonDto());

        assertNotNull(headers);
        assertEquals(3, headers.length);
        assertEquals("first_name", headers[0]);
        assertEquals("last_name", headers[1]);
        assertEquals("age", headers[2]);
    }

    @Test
    void testGenerateHeaderWithNullBean() {
        OrderedHeaderColumnNameMappingStrategy<PersonDto> strategy = new OrderedHeaderColumnNameMappingStrategy<>();
        strategy.setType(PersonDto.class);

        assertDoesNotThrow(() -> strategy.generateHeader(null));
    }

    @Test
    void testGenerateHeaderWithMissingAnnotationAndComparator() throws Exception {
        class PartialDto {
            @CsvBindByName(column = "")
            private String field1;

            private String field2;
        }

        OrderedHeaderColumnNameMappingStrategy<PartialDto> strategy = new OrderedHeaderColumnNameMappingStrategy<>() {
        };
        strategy.setType(PartialDto.class);

        String[] headers = strategy.generateHeader(new PartialDto());

        assertNotNull(headers);
        assertEquals(1, headers.length);
        assertEquals("field1", headers[0]);

        Comparator<String> comparator = Comparator.comparingInt(header -> {
            int idx = Arrays.asList(headers).indexOf(header);
            return (idx == -1) ? Integer.MAX_VALUE : idx;
        });

        assertEquals(0, comparator.compare("field1", "field1"));
        assertTrue(comparator.compare("unknown", "field1") > 0);
    }

    @Test
    void testGenerateHeaderRespectsCsvIgnore() throws Exception {
        class ExampleDto {
            @CsvBindByName(column = "included")
            private String included;

            @CsvBindByName(column = "ignoredAlways")
            @CsvIgnore
            private String ignoredAlways;

            @CsvBindByName(column = "ignoredForProfile")
            @CsvIgnore(profiles = "PROFILE_X")
            private String ignoredForProfile;
        }

        OrderedHeaderColumnNameMappingStrategy<ExampleDto> strategy = new OrderedHeaderColumnNameMappingStrategy<>();
        strategy.setType(ExampleDto.class);

        // Case 1: without profile → ignore only "ignoredAlways"
        String[] headers = strategy.generateHeader(new ExampleDto());
        assertEquals(2, headers.length);
        assertTrue(Arrays.asList(headers).contains("included"));
        assertTrue(Arrays.asList(headers).contains("ignoredForProfile"));
        assertFalse(Arrays.asList(headers).contains("ignoredAlways"));

        // Case 2: with profile PROFILE_X → ignore even "ignoredForProfile"
        strategy.setActiveProfile("PROFILE_X");
        headers = strategy.generateHeader(new ExampleDto());
        assertEquals(1, headers.length);
        assertEquals("included", headers[0]);
    }
}
