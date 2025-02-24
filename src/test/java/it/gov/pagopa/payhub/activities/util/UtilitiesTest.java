package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
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
    void testToOffsetDateTime() throws DatatypeConfigurationException {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(now.withOffsetSameInstant(ZoneOffset.UTC).toString());
        // When
        OffsetDateTime result = Utilities.toOffsetDateTime(date);
        // Then
        assertNotNull(result);
        assertEquals(now.getYear(), result.getYear());
        assertEquals(now.getMonthValue(), result.getMonthValue());
        assertEquals(now.getDayOfMonth(), result.getDayOfMonth());
        assertEquals(now.getHour(), result.getHour());
        assertEquals(now.getMinute(), result.getMinute());
        assertEquals(now.getSecond(), result.getSecond());
    }
    @Test
    void testToOffsetDateTimeNull(){
        // When
        OffsetDateTime result = Utilities.toOffsetDateTime(null);
        // Then
        assertNull(result);
    }
}
