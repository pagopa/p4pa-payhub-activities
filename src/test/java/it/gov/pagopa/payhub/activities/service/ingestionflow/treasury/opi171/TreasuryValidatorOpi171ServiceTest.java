package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi171;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi171.FlussoGiornaleDiCassa;
import java.io.File;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TreasuryValidatorOpi171ServiceTest {

    private TreasuryValidatorOpi171Service treasuryValidatorService171;
    private FlussoGiornaleDiCassa mockFlussoV171, mockFlussoV171NoIufNoIuv, mockFlussoV171NoEsercizio;
    private File mockFile;

    @BeforeEach
    void setUp() {
        treasuryValidatorService171 = new TreasuryValidatorOpi171Service();
        
        mockFlussoV171 = new FlussoGiornaleDiCassa();
        mockFlussoV171.getEsercizio().add(2024);
        mockFlussoV171.getPagineTotali().add(2);
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza informazioniContoEvidenza171 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza171 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza171.setCausale("ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_TEST_RFS12345678901234567891234567890123456789213456789234567892345t6y7890 RFB oh948jgvndfsjvhfugf089rweuvjnfeeoknjbv908354ug890uboinfk4j2-90rui354809g4truihbnr4gf-90o43uitg089435huighn53riog345r09ugf80453yg9r4thior4tg0ir4");
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare sospesoDaRegolarizzare171 = new it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare();
        movimentoContoEvidenza171.setSospesoDaRegolarizzare(sospesoDaRegolarizzare171);
        movimentoContoEvidenza171.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza171.getMovimentoContoEvidenzas().add(movimentoContoEvidenza171);
        mockFlussoV171.getInformazioniContoEvidenza().add(informazioniContoEvidenza171);

        mockFile = new File("testFile.xml");

        mockFlussoV171NoIufNoIuv = new FlussoGiornaleDiCassa();
        mockFlussoV171NoIufNoIuv.getEsercizio().add(2024);
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza informazioniContoEvidenza171NoIufNoIuv = new it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza171NoIufNoIuv = new it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza171NoIufNoIuv.setCausale("ACCREDITI VARI");
        movimentoContoEvidenza171NoIufNoIuv.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza171NoIufNoIuv.getMovimentoContoEvidenzas().add(movimentoContoEvidenza171NoIufNoIuv);
        mockFlussoV171NoIufNoIuv.getInformazioniContoEvidenza().add(informazioniContoEvidenza171NoIufNoIuv);

        mockFlussoV171NoEsercizio = new FlussoGiornaleDiCassa();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza informazioniContoEvidenza171NoEsercizio = new it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza171NoEsercizio = new it.gov.pagopa.payhub.activities.xsd.treasury.opi171.InformazioniContoEvidenza.MovimentoContoEvidenza();
        informazioniContoEvidenza171NoEsercizio.getMovimentoContoEvidenzas().add(movimentoContoEvidenza171NoEsercizio);
        mockFlussoV171NoEsercizio.getInformazioniContoEvidenza().add(informazioniContoEvidenza171NoEsercizio);

    }

    @Test
    void validateDataV16() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV171;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService171.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV171NoIufNoIuv() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV171NoIufNoIuv;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService171.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
        assertEquals("Tipo operazione field is not valorized but it is required", result.get(2).getErrorMessage());
    }


    @Test
    void validateDataV171NoEsercizio() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV171NoEsercizio;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService171.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(12, result.size());

        assertEquals("Esercizio field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo movimento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validatePageSize_Ko() {
        //Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV171;

        //When
        boolean res= treasuryValidatorService171.validatePageSize(flussoGiornaleDiCassa,6);

        //Then
        assertFalse(res);
    }

}
