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
    void testGetIdentificativo_withValidXlsIUF() {
        // Given
        String input = "Data Ordine: 01/01/2020; Descrizione Ordinante: XYZ PRIVATE BANKING SPA                          PIAZZA SAN :BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072601 :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM";
        String type = TreasuryUtils.IUF;

        // When
        String result = TreasuryUtils.getIdentificativo(input, type);

        // Then
        assertNotNull(result);
        assertEquals("2024-07-26PPAYITR1XXX-S2024072601", result);
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

    @Test
    void testGetPspLastName_withValidXlsTreasury() {
        // Given
        String input = "Data Ordine: 01/01/2020; Descrizione Ordinante: XYZ PRIVATE BANKING SPA                          PIAZZA SAN :BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072601 :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM";

        // When
        String result = TreasuryUtils.getPspLastName(input);

        // Then
        assertNotNull(result);
        assertEquals("XYZ PRIVATE BANKING SPA                          PIAZZA SAN", result);
    }

    @Test
    void testGetPspLastName_withInvalidXlsTreasury_emptyPspLastName_returnNull() {
        // Given
        String input = "Data Ordine: 01/01/2020; Descrizione Ordinante::BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072601 :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM";

        // When
        String result = TreasuryUtils.getPspLastName(input);

        // Then
        assertNull(result);
    }

    @Test
    void testGetPspLastName_withInvalidXlsTreasury_blankPspLastName_returnNull() {
        // Given
        String input = "Data Ordine: 01/01/2020; Descrizione Ordinante:   :BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072601 :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM";

        // When
        String result = TreasuryUtils.getPspLastName(input);

        // Then
        assertNull(result);
    }

    @Test
    void testGetPspLastName_withInvalidXlsTreasury_emptyExtendedRemittanceDescription_returnNull() {
        // Given
        String input = "";

        // When
        String result = TreasuryUtils.getPspLastName(input);

        // Then
        assertNull(result);
    }
}