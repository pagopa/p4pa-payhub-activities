package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class CsvOffsetDateTimeConverterTest {

    private CsvOffsetDateTimeConverter converter;

    @BeforeEach
    void setUp(){
        converter = new CsvOffsetDateTimeConverter();
    }

    @Test
    void givenValidDateString_whenConvert_thenReturnsOffsetDateTime() {
        
        String dateString = "2025-02-20";
        
        OffsetDateTime result = converter.convert(dateString);
        
        assertNotNull(result);
        assertEquals(LocalDate.of(2025, 2, 20).atStartOfDay().atOffset(ZoneOffset.UTC), result);
    }

    @Test
    void givenNullString_whenConvert_thenReturnsNull() {
        OffsetDateTime result = converter.convert(null);

        assertNull(result);
    }

    @Test
    void givenEmptyString_whenConvert_thenReturnsNull() {
        
        String emptyString = "";

        OffsetDateTime result = converter.convert(emptyString);

        assertNull(result);
    }

    @Test
    void givenInvalidDateString_whenConvert_thenThrowsException() {
        
        String invalidDateString = "invalid-date";

        assertThrows(Exception.class, () -> converter.convert(invalidDateString));
    }
}

