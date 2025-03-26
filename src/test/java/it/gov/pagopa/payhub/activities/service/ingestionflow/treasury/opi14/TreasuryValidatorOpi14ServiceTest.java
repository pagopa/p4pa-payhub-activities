package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi14;


import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorFileDTO;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreasuryValidatorOpi14ServiceTest {

    private TreasuryValidatorOpi14Service treasuryValidatorService14;
    private FlussoGiornaleDiCassa mockFlussoV14, mockFlussoV14NoIufNoIuv, mockFlussoV14NoEsercizio;
    private File mockFile;

    @BeforeEach
    void setUp() {
        treasuryValidatorService14 = new TreasuryValidatorOpi14Service();
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

        mockFile = new File("testFile.xml");

        mockFlussoV14NoIufNoIuv = new FlussoGiornaleDiCassa();
        mockFlussoV14NoIufNoIuv.getEsercizio().add(2024);
        InformazioniContoEvidenza informazioniContoEvidenza14NoIufNoIuv = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza14NoIufNoIuv = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza14NoIufNoIuv.setCausale("ACCREDITI VARI");
        movimentoContoEvidenza14NoIufNoIuv.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza14NoIufNoIuv.getMovimentoContoEvidenzas().add(movimentoContoEvidenza14NoIufNoIuv);
        mockFlussoV14NoIufNoIuv.getInformazioniContoEvidenza().add(informazioniContoEvidenza14NoIufNoIuv);

        mockFlussoV14NoEsercizio = new FlussoGiornaleDiCassa();
        InformazioniContoEvidenza informazioniContoEvidenza14NoEsercizio = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza14NoEsercizio = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        informazioniContoEvidenza14NoEsercizio.getMovimentoContoEvidenzas().add(movimentoContoEvidenza14NoEsercizio);
        mockFlussoV14NoEsercizio.getInformazioniContoEvidenza().add(informazioniContoEvidenza14NoEsercizio);
    }

    @Test
    void validateDataV14() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV14;

        // When
        List<TreasuryErrorFileDTO> result = treasuryValidatorService14.validateData(flussoGiornaleDiCassa,  mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV14NoIufNoIuv() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV14NoIufNoIuv;

        // When
        List<TreasuryErrorFileDTO> result = treasuryValidatorService14.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV14NoEsercizio() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV14NoEsercizio;

        // When
        List<TreasuryErrorFileDTO> result = treasuryValidatorService14.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertEquals(12, result.size());

        assertEquals("Esercizio field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo movimento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validatePageSize_Ok() {
        //Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV14;

        //When
        boolean res= treasuryValidatorService14.validatePageSize(flussoGiornaleDiCassa,2);

        //Then
        assertTrue(res);
    }

    @Test
    void validatePageSize_KoWithNullFgc() {
        //When
        boolean res= treasuryValidatorService14.validatePageSize(null,6);

        //Then
        assertFalse(res);
    }

}
