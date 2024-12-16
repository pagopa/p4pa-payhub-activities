package it.gov.pagopa.payhub.activities.service.treasury;


import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreasuryValidatorServiceTest {

    private TreasuryValidatorService treasuryValidatorService;
    private FlussoGiornaleDiCassa mockFlussoV14;
    private FlussoGiornaleDiCassa mockFlussoV14NoIufNoIuv;
    private it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa mockFlussoV161;
    private it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa mockFlussoV161NoIufNoIuv;
    private File mockFile;

    @BeforeEach
    void setUp() {
        treasuryValidatorService = new TreasuryValidatorService();
        mockFlussoV14 = new FlussoGiornaleDiCassa();
        mockFlussoV14.getEsercizio().add(2024);
        InformazioniContoEvidenza informazioniContoEvidenza14 = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza14 = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza14.setCausale("ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_TEST_RFS12345678901234567891234567890123456789213456789234567892345t6y7890 RFB oh948jgvndfsjvhfugf089rweuvjnfeeoknjbv908354ug890uboinfk4j2-90rui354809g4truihbnr4gf-90o43uitg089435huighn53riog345r09ugf80453yg9r4thior4tg0ir4");
        movimentoContoEvidenza14.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza14.getMovimentoContoEvidenzas().add(movimentoContoEvidenza14);
        mockFlussoV14.getInformazioniContoEvidenza().add(informazioniContoEvidenza14);

        mockFlussoV161 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa();
        mockFlussoV161.getEsercizio().add(2024);
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza informazioniContoEvidenza161 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza161 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza161.setCausale("ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_TEST_RFS12345678901234567891234567890123456789213456789234567892345t6y7890 RFB oh948jgvndfsjvhfugf089rweuvjnfeeoknjbv908354ug890uboinfk4j2-90rui354809g4truihbnr4gf-90o43uitg089435huighn53riog345r09ugf80453yg9r4thior4tg0ir4");
        movimentoContoEvidenza161.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza161.getMovimentoContoEvidenzas().add(movimentoContoEvidenza161);
        mockFlussoV161.getInformazioniContoEvidenza().add(informazioniContoEvidenza161);

        mockFile = new File("testFile.xml");

        mockFlussoV14NoIufNoIuv = new FlussoGiornaleDiCassa();
        mockFlussoV14NoIufNoIuv.getEsercizio().add(2024);
        InformazioniContoEvidenza informazioniContoEvidenza14NoIufNoIuv = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza14NoIufNoIuv = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza14NoIufNoIuv.setCausale("ACCREDITI VARI");
        movimentoContoEvidenza14NoIufNoIuv.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza14NoIufNoIuv.getMovimentoContoEvidenzas().add(movimentoContoEvidenza14NoIufNoIuv);
        mockFlussoV14NoIufNoIuv.getInformazioniContoEvidenza().add(informazioniContoEvidenza14NoIufNoIuv);

        mockFlussoV161NoIufNoIuv = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa();
        mockFlussoV161NoIufNoIuv.getEsercizio().add(2024);
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza informazioniContoEvidenza161NoIufNoIuv = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza161NoIufNoIuv = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza161NoIufNoIuv.setCausale("ACCREDITI VARI");
        movimentoContoEvidenza161NoIufNoIuv.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza161NoIufNoIuv.getMovimentoContoEvidenzas().add(movimentoContoEvidenza161NoIufNoIuv);
        mockFlussoV161NoIufNoIuv.getInformazioniContoEvidenza().add(informazioniContoEvidenza161NoIufNoIuv);

    }

    @Test
    void validateDataV14() {
        // Given
        //when(mockFlussoV14.getInformazioniContoEvidenza()).thenReturn(mockFlussoV14.getInformazioniContoEvidenza());
       // when(mockFlussoV161.getInformazioniContoEvidenza()).thenReturn(Collections.singletonList("AnotherExceedinglyLongStringThatShouldTriggerAnErrorBecauseItIsTooLong"));

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(mockFlussoV14, mockFlussoV161, mockFile, TreasuryValidatorService.v14);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(11, result.size());

        assertEquals("Codice univoco Flusso exceed max length of 35 chars", result.get(0).getErrorMessage());
        assertEquals("Codice univoco Versamento exceed max length of 35 chars", result.get(1).getErrorMessage());
        assertEquals("Tipo movimento field is not valorized but it is required", result.get(2).getErrorMessage());
    }

    @Test
    void validateDataV14NoIufNoIuv() {
        // Given
        //when(mockFlussoV14.getInformazioniContoEvidenza()).thenReturn(mockFlussoV14.getInformazioniContoEvidenza());
       // when(mockFlussoV161.getInformazioniContoEvidenza()).thenReturn(Collections.singletonList("AnotherExceedinglyLongStringThatShouldTriggerAnErrorBecauseItIsTooLong"));

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(mockFlussoV14NoIufNoIuv, mockFlussoV161, mockFile, TreasuryValidatorService.v14);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(11, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV16() {
        // Given
        //when(mockFlussoV14.getInformazioniContoEvidenza()).thenReturn(mockFlussoV14.getInformazioniContoEvidenza());
        // when(mockFlussoV161.getInformazioniContoEvidenza()).thenReturn(Collections.singletonList("AnotherExceedinglyLongStringThatShouldTriggerAnErrorBecauseItIsTooLong"));

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(mockFlussoV14, mockFlussoV161, mockFile, TreasuryValidatorService.v161);

        // Then
        assertNotNull(result);
        //assertFalse(result.isEmpty());
        assertEquals(11, result.size());

        assertEquals("Codice univoco Flusso exceed max length of 35 chars", result.get(0).getErrorMessage());
        assertEquals("Codice univoco Versamento exceed max length of 35 chars", result.get(1).getErrorMessage());
        assertEquals("Tipo movimento field is not valorized but it is required", result.get(2).getErrorMessage());
    }

    @Test
    void validateDataV161NoIufNoIuv() {
        // Given
        //when(mockFlussoV14.getInformazioniContoEvidenza()).thenReturn(mockFlussoV14.getInformazioniContoEvidenza());
        // when(mockFlussoV161.getInformazioniContoEvidenza()).thenReturn(Collections.singletonList("AnotherExceedinglyLongStringThatShouldTriggerAnErrorBecauseItIsTooLong"));

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(mockFlussoV14NoIufNoIuv, mockFlussoV161, mockFile, TreasuryValidatorService.v161);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(11, result.size());

        assertEquals("Codice univoco Flusso exceed max length of 35 chars", result.get(0).getErrorMessage());
        assertEquals("Codice univoco Versamento exceed max length of 35 chars", result.get(1).getErrorMessage());
        assertEquals("Tipo movimento field is not valorized but it is required", result.get(2).getErrorMessage());
    }


}
