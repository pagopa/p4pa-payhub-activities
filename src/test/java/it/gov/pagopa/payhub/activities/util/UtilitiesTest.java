package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class UtilitiesTest {

    @Test
    void testIbanInvalid(){
        String iban = "test";
        boolean result = Utilities.isValidIban(iban);
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12345", "12345abc123", "1234/abc123"})
    void testValidateEmptyPIVA(String piva){
        boolean result = Utilities.isValidPIVA(piva);
        assertFalse(result);
    }

    @Test
    void testBigDecimalEuroToLongCentsAmount(){
        // Given
        BigDecimal amount = BigDecimal.valueOf(123.45);
        // When
        long result = Utilities.bigDecimalEuroToLongCentsAmount(amount);
        // Then
        assertEquals(12345, result);
    }
    @Test
    void testBigDecimalEuroToLongCentsAmountNull(){
        // When
        Long result = Utilities.bigDecimalEuroToLongCentsAmount(null);
        // Then
        assertNull(result);
    }

    @Test
    void testToXMLGregorianCalendar(){
        // Given
        OffsetDateTime date = OffsetDateTime.now();
        // When
        XMLGregorianCalendar result = Utilities.toXMLGregorianCalendar(date);
        // Then
        assertNotNull(result);
        assertEquals(date.getYear(), result.toGregorianCalendar().get(Calendar.YEAR));
        assertEquals(date.getMonthValue()-1, result.toGregorianCalendar().get(Calendar.MONTH));
        assertEquals(date.getDayOfMonth(), result.toGregorianCalendar().get(Calendar.DAY_OF_MONTH));
        assertEquals(date.getHour(), result.toGregorianCalendar().get(Calendar.HOUR_OF_DAY));
        assertEquals(date.getMinute(), result.toGregorianCalendar().get(Calendar.MINUTE));
        assertEquals(date.getSecond(), result.toGregorianCalendar().get(Calendar.SECOND));
    }

    @Test
    void testToXMLGregorianCalendarNull(){
        // When
        XMLGregorianCalendar result = Utilities.toXMLGregorianCalendar(null);
        // Then
        assertNull(result);
    }

    @Test
    void testToOffsetDateTimeWithXMLGregorianCalendar() throws DatatypeConfigurationException {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(now.withOffsetSameInstant(ZoneOffset.UTC).toString());
        OffsetDateTime result = Utilities.toOffsetDateTime(date);
        assertConversion(now, result);
    }

    @Test
    void testToOffsetDateTimeWithLocalDateTime() {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        LocalDateTime date = now.toLocalDateTime();
        OffsetDateTime result = Utilities.toOffsetDateTime(date);
        assertConversion(now, result);
    }

    @Test
    void testToOffsetDateTimeWithLocalDate() {
        // Given
        OffsetDateTime now = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDate date = now.toLocalDate();
        OffsetDateTime result = Utilities.toOffsetDateTime(date);
        assertConversion(now, result);
    }

    @Test
    void givenNullDatesWhenTestToOffsetDateTimeThenAssertNull(){
        // Given
        LocalDate localDate = null;
        LocalDateTime localDateTime = null;
        XMLGregorianCalendar xmlGregorianCalendar = null;
        // When Then
        assertNull(Utilities.toOffsetDateTime(localDate));
        assertNull(Utilities.toOffsetDateTime(localDateTime));
        assertNull(Utilities.toOffsetDateTime(xmlGregorianCalendar));
    }

    private <T> void assertConversion(OffsetDateTime expected, OffsetDateTime result) {
        // Then
        assertNotNull(result);
        assertEquals(expected.getYear(), result.getYear());
        assertEquals(expected.getMonthValue(), result.getMonthValue());
        assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
        assertEquals(expected.getHour(), result.getHour());
        assertEquals(expected.getMinute(), result.getMinute());
        assertEquals(expected.getSecond(), result.getSecond());
    }
}
