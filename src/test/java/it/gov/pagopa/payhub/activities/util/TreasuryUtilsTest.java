package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TreasuryUtilsTest {

    @Test
    void testGetIdentificativo_withIUF() {
        // Give
        String value = "ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_VALID";

        // When
        String result = TreasuryUtils.getIdentificativo(value, TreasuryUtils.IUF);

        // Then
        assertNotNull(result);
        assertEquals("2024-12-15IUV_VALID", result);
    }

    @Test
    void testGetIdentificativo_withIUV() {
        // Give
        String value = "RFS/URI/2024-12-15 IUV_TEST";

        // When
        String result = TreasuryUtils.getIdentificativo(value, TreasuryUtils.IUV);

        // Then
        assertNotNull(result);
        assertEquals("URI", result);
    }

    @Test
    void testGetIdentificativo_withEmptyValue() {
        // Given
        String value = "";

        // When
        String result = TreasuryUtils.getIdentificativo(value, TreasuryUtils.IUF);

        // Then
        assertNull(result);
    }

    @Test
    void testGetIdentificativo_withNullValue() {
        // Given
        String value = null;

        // When
        String result = TreasuryUtils.getIdentificativo(value, TreasuryUtils.IUF);

        // Then
        assertNull(result);
    }

    @Test
    void testGetIdentificativo_withIUVAndNoMatch() {
        // Given
        String value = "IUV_NOT_MATCH";

        // When
        String result = TreasuryUtils.getIdentificativo(value, TreasuryUtils.IUV);

        // Then
        assertNull(result);
    }

    @Test
    void testGetDataFromIuf_validDate() {
        // Give
        String value = "IUF_TEST 2024-12-15 VALID_DATE";

        // When
        String result = TreasuryUtils.getDataFromIuf(value);

        // Then
        assertNotNull(result);
        assertEquals("2024-12-15", result);
    }

    @Test
    void testGetDataFromIuf_noDate() {
        // Give
        String value = "IUF_TEST_NO_DATE";

        // When
        String result = TreasuryUtils.getDataFromIuf(value);

        // Then
        assertNull(result);
    }

    @Test
    void testCheckIufOld_validString() {
        // Give
        String value = "Valid string without special characters";

        // When
        boolean result = TreasuryUtils.checkIufOld(value);

        // Then
        assertTrue(result);
    }

    @Test
    void testCheckIufOld_invalidString() {
        // Give
        String value = "Invalid string with special characters !@#";

        // When
        boolean result = TreasuryUtils.checkIufOld(value);

        // Then
        assertFalse(result);
    }

    @Test
    void testCheckIufOld_emptyString() {
        // Give
        String value = "";

        // When
        boolean result = TreasuryUtils.checkIufOld(value);

        // Then
        assertTrue(result);
    }

    @Test
    void testCheckIufOld_nullString() {
        // Give
        String value = null;

        // When
        boolean result = TreasuryUtils.checkIufOld(value);

        // Then
        assertTrue(result);
    }

}
