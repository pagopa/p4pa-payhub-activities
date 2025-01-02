package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreasuryUtilsTest {

    @Test
    void testGetIdentificativo_withValidIUF() {
        // Given
        String input = "ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2023-01-01 ABC123";
        String type = TreasuryUtils.IUF;

        // When
        String result = TreasuryUtils.getIdentificativo(input, type);

        // Then
        assertNotNull(result);
        assertEquals("2023-01-01ABC123", result);
    }

    @Test
    void testGetIdentificativo_withInvalidIUF() {
        // Given
        String input = "INVALID STRING";
        String type = TreasuryUtils.IUF;

        // When
        String result = TreasuryUtils.getIdentificativo(input, type);

        // Then
        assertNull(result);
    }

    @Test
    void testGetDataFromIuf_withValidDate() {
        // Given
        String input = "2023-01-01ABC123";

        // When
        String result = TreasuryUtils.getDataFromIuf(input);

        // Then
        assertNotNull(result);
        assertEquals("2023-01-01", result);
    }

    @Test
    void testGetDataFromIuf_withNoDate() {
        // Given
        String input = "ABC123";

        // When
        String result = TreasuryUtils.getDataFromIuf(input);

        // Then
        assertNull(result);
    }

    @Test
    void testCheckIufOld_withOldFormat() {
        // Given
        String input = "ABC123";

        // When
        boolean result = TreasuryUtils.checkIufOld(input);

        // Then
        assertTrue(result);
    }

    @Test
    void testCheckIufOld_withNewFormat() {
        // Given
        String input = "ABC@123";

        // When
        boolean result = TreasuryUtils.checkIufOld(input);

        // Then
        assertFalse(result);
    }
}