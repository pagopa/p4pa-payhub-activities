package it.gov.pagopa.payhub.activities.service.files.xls;

import com.opencsv.bean.CsvBindByName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        assertEquals(Arrays.asList("first_name", "last_name", "age"), Arrays.asList(headers), "The order of the fields should be the same as the DTO");
    }

    @Test
    void testGenerateHeaderWithNullBean() {
        OrderedHeaderColumnNameMappingStrategy<PersonDto> strategy = new OrderedHeaderColumnNameMappingStrategy<>();
        strategy.setType(PersonDto.class);

        assertDoesNotThrow(() -> strategy.generateHeader(null));
    }
}
