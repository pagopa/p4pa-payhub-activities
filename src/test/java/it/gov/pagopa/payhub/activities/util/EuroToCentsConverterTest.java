package it.gov.pagopa.payhub.activities.util;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EuroToCentsConverterTest {
    private EuroToCentsConverter converter;

    @BeforeEach
    void setUp() {
        converter = new EuroToCentsConverter();
    }

    @Test
    void givenEuroWithDecimalPointWhenConvertThenReturnCorrectCents() throws CsvDataTypeMismatchException {
        String euroValue = "12.34";
        Long expectedCents = 1234L;

        Object result = converter.convert(euroValue);

        assertNotNull(result);
        assertEquals(expectedCents, result);
    }

    @Test
    void givenEuroWithCommaWhenConvertThenReturnCorrectCents() throws CsvDataTypeMismatchException {
        String euroValue = "56,78";
        Long expectedCents = 5678L;

        Object result = converter.convert(euroValue);

        assertNotNull(result);
        assertEquals(expectedCents, result);
    }

    @Test
    void givenInvalidFormatWhenConvertThenThrowCsvDataTypeMismatchException() {
        String invalidValue = "abcde";

        CsvDataTypeMismatchException exception = assertThrows(CsvDataTypeMismatchException.class, () -> {
            converter.convert(invalidValue);
        });

        assertEquals("Could not convert value 'abcde' to cents", exception.getMessage());
    }

    @Test
    void givenBlankInputWhenConvertThenReturnNull() throws CsvDataTypeMismatchException {
        String blankValue = "";

        Object result = converter.convert(blankValue);

        assertNull(result);
    }
}
