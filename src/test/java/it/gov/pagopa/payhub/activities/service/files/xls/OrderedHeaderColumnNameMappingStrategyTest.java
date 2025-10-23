package it.gov.pagopa.payhub.activities.service.files.xls;

import com.opencsv.bean.CsvBindByName;
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
}
