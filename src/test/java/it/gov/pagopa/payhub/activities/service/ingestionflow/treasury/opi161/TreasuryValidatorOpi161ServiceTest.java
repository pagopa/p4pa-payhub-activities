package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi161;


import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreasuryValidatorOpi161ServiceTest {

    private TreasuryValidatorOpi161Service treasuryValidatorService161;
    private FlussoGiornaleDiCassa mockFlussoV161, mockFlussoV161NoIufNoIuv, mockFlussoV161NoEsercizio;
    private File mockFile;

    @BeforeEach
    void setUp() {
        treasuryValidatorService161 = new TreasuryValidatorOpi161Service();

        mockFlussoV161 = new FlussoGiornaleDiCassa();
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

        mockFlussoV161NoIufNoIuv = new FlussoGiornaleDiCassa();
        mockFlussoV161NoIufNoIuv.getEsercizio().add(2024);
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza informazioniContoEvidenza161NoIufNoIuv = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza161NoIufNoIuv = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza161NoIufNoIuv.setCausale("ACCREDITI VARI");
        movimentoContoEvidenza161NoIufNoIuv.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza161NoIufNoIuv.getMovimentoContoEvidenzas().add(movimentoContoEvidenza161NoIufNoIuv);
        mockFlussoV161NoIufNoIuv.getInformazioniContoEvidenza().add(informazioniContoEvidenza161NoIufNoIuv);

        mockFlussoV161NoEsercizio = new FlussoGiornaleDiCassa();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza informazioniContoEvidenza161NoEsercizio = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza();
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza161NoEsercizio = new it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza.MovimentoContoEvidenza();
        informazioniContoEvidenza161NoEsercizio.getMovimentoContoEvidenzas().add(movimentoContoEvidenza161NoEsercizio);
        mockFlussoV161NoEsercizio.getInformazioniContoEvidenza().add(informazioniContoEvidenza161NoEsercizio);

    }

    @Test
    void validateDataV16() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV161;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService161.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV161NoIufNoIuv() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV161NoIufNoIuv;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService161.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
        assertEquals("Tipo operazione field is not valorized but it is required", result.get(2).getErrorMessage());
    }


    @Test
    void validateDataV161NoEsercizio() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa= mockFlussoV161NoEsercizio;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService161.validateData(flussoGiornaleDiCassa, mockFile.getName());

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
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV161;

        //When
        boolean res= treasuryValidatorService161.validatePageSize(flussoGiornaleDiCassa,6);

        //Then
        assertFalse(res);
    }

}
