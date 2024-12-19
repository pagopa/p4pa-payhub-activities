package it.gov.pagopa.payhub.activities.service.treasury;


import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreasuryValidatorServiceTest {

    private TreasuryValidatorService treasuryValidatorService;
    private FlussoGiornaleDiCassa mockFlussoV14, mockFlussoV14NoIufNoIuv, mockFlussoV14NoEsercizio;
    private it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa mockFlussoV161, mockFlussoV161NoIufNoIuv, mockFlussoV161NoEsercizio;
    private File mockFile;

    @BeforeEach
    void setUp() {
        treasuryValidatorService = new TreasuryValidatorService();
        mockFlussoV14 = new FlussoGiornaleDiCassa();
        mockFlussoV14.getEsercizio().add(2024);
        mockFlussoV14.getPagineTotali().add(2);
        InformazioniContoEvidenza informazioniContoEvidenza14 = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza14 = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza14.setCausale("ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_TEST_RFS12345678901234567891234567890123456789213456789234567892345t6y7890 RFB oh948jgvndfsjvhfugf089rweuvjnfeeoknjbv908354ug890uboinfk4j2-90rui354809g4truihbnr4gf-90o43uitg089435huighn53riog345r09ugf80453yg9r4thior4tg0ir4");
        InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare sospesoDaRegolarizzare14= new InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare();
        movimentoContoEvidenza14.setNumeroBollettaQuietanza(new BigInteger("999"));
        movimentoContoEvidenza14.setSospesoDaRegolarizzare(sospesoDaRegolarizzare14);
        informazioniContoEvidenza14.getMovimentoContoEvidenzas().add(movimentoContoEvidenza14);
        mockFlussoV14.getInformazioniContoEvidenza().add(informazioniContoEvidenza14);

        mockFlussoV161 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa();
        mockFlussoV161.getEsercizio().add(2024);
        mockFlussoV161.getPagineTotali().add(2);
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza informazioniContoEvidenza161 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza161 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza161.setCausale("ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_TEST_RFS12345678901234567891234567890123456789213456789234567892345t6y7890 RFB oh948jgvndfsjvhfugf089rweuvjnfeeoknjbv908354ug890uboinfk4j2-90rui354809g4truihbnr4gf-90o43uitg089435huighn53riog345r09ugf80453yg9r4thior4tg0ir4");
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare sospesoDaRegolarizzare161 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare();
        movimentoContoEvidenza161.setSospesoDaRegolarizzare(sospesoDaRegolarizzare161);
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

        mockFlussoV14NoEsercizio = new FlussoGiornaleDiCassa();
        InformazioniContoEvidenza informazioniContoEvidenza14NoEsercizio = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza14NoEsercizio = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        informazioniContoEvidenza14NoEsercizio.getMovimentoContoEvidenzas().add(movimentoContoEvidenza14NoEsercizio);
        mockFlussoV14NoEsercizio.getInformazioniContoEvidenza().add(informazioniContoEvidenza14NoEsercizio);

        mockFlussoV161NoEsercizio = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza informazioniContoEvidenza161NoEsercizio = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza161NoEsercizio = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza();
        informazioniContoEvidenza161NoEsercizio.getMovimentoContoEvidenzas().add(movimentoContoEvidenza161NoEsercizio);
        mockFlussoV161NoEsercizio.getInformazioniContoEvidenza().add(informazioniContoEvidenza161NoEsercizio);

    }

    @Test
    void validateDataV14() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV14;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(flussoGiornaleDiCassa,  mockFile, TreasuryValidatorService.V_14);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Codice univoco Flusso exceed max length of 35 chars", result.get(0).getErrorMessage());
        assertEquals("Codice univoco Versamento exceed max length of 35 chars", result.get(1).getErrorMessage());
        assertEquals("TipoMovimento field is not valorized but it is required", result.get(2).getErrorMessage());
    }

    @Test
    void validateDataV14NoIufNoIuv() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV14NoIufNoIuv;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(flussoGiornaleDiCassa, mockFile, TreasuryValidatorService.V_14);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(8, result.size());

        assertEquals("TipoMovimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("TipoDocumento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV14NoEsercizio() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV14NoEsercizio;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(flussoGiornaleDiCassa, mockFile, TreasuryValidatorService.V_14);

        // Then
        assertNotNull(result);
        //assertFalse(result.isEmpty());
        assertEquals(9, result.size());

        assertEquals("TipoMovimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("TipoDocumento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV16() {
        // Given
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV161;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(flussoGiornaleDiCassa, mockFile, TreasuryValidatorService.V_161);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Codice univoco Flusso exceed max length of 35 chars", result.get(0).getErrorMessage());
        assertEquals("Codice univoco Versamento exceed max length of 35 chars", result.get(1).getErrorMessage());
        assertEquals("TipoMovimento field is not valorized but it is required", result.get(2).getErrorMessage());
    }

    @Test
    void validateDataV161NoIufNoIuv() {
        // Given
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV161NoIufNoIuv;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(flussoGiornaleDiCassa, mockFile, TreasuryValidatorService.V_161);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(8, result.size());

        assertEquals("TipoMovimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("TipoDocumento field is not valorized but it is required", result.get(1).getErrorMessage());
        assertEquals("TipoOperazione field is not valorized but it is required", result.get(2).getErrorMessage());
    }


    @Test
    void validateDataV161NoEsercizio() {
        // Given
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV161NoEsercizio;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService.validateData(flussoGiornaleDiCassa, mockFile, TreasuryValidatorService.V_161);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(9, result.size());

        assertEquals("TipoMovimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("TipoDocumento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validatePageSize_Ok() {
        //Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV14;

        //When
        boolean res=treasuryValidatorService.validatePageSize(flussoGiornaleDiCassa,null,2,TreasuryValidatorService.V_14);

        //Then
        assertTrue(res);
    }

    @Test
    void validatePageSize_Ko() {
        //Given
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV161;

        //When
        boolean res=treasuryValidatorService.validatePageSize(null,flussoGiornaleDiCassa,6,TreasuryValidatorService.V_161);

        //Then
        assertFalse(res);
    }

    @Test
    void validatePageSize_KoWithNullFgc() {
        //Given
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV161;

        //When
        boolean res=treasuryValidatorService.validatePageSize(null,null,6,TreasuryValidatorService.V_161);

        //Then
        assertFalse(res);
    }

}
