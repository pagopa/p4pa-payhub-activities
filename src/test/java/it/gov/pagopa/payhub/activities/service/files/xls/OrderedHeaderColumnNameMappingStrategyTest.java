package it.gov.pagopa.payhub.activities.service.files.xls;

import com.opencsv.bean.CsvBindByName;
import org.junit.jupiter.api.Test;

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
        OrderedHeaderColumnNameMappingStrategy<PersonDto> strategy = new OrderedHeaderColumnNameMappingStrategy<>();
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
}
