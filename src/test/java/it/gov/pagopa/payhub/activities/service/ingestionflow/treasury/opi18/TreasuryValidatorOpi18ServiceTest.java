package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi18;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi18.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi18.InformazioniContoEvidenza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreasuryValidatorOpi18ServiceTest {
    private TreasuryValidatorOpi18Service treasuryValidatorService18;
    private FlussoGiornaleDiCassa mockFlussoV18, mockFlussoV18NoIufNoIuv, mockFlussoV18NoEsercizio;
    private File mockFile;

    @BeforeEach
    void setUp() {
        treasuryValidatorService18 = new TreasuryValidatorOpi18Service();

        mockFlussoV18 = new FlussoGiornaleDiCassa();
        mockFlussoV18.getEsercizio().add(2024);
        mockFlussoV18.getPagineTotali().add(2);
        InformazioniContoEvidenza informazioniContoEvidenza18 = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza18 = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza18.setCausale("ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_TEST_RFS12345678901234567891234567890123456789213456789234567892345t6y7890 RFB oh948jgvndfsjvhfugf089rweuvjnfeeoknjbv908354ug890uboinfk4j2-90rui354809g4truihbnr4gf-90o43uitg089435huighn53riog345r09ugf80453yg9r4thior4tg0ir4");
        InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare sospesoDaRegolarizzare18 = new InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare();
        movimentoContoEvidenza18.setSospesoDaRegolarizzare(sospesoDaRegolarizzare18);
        movimentoContoEvidenza18.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza18.getMovimentoContoEvidenzas().add(movimentoContoEvidenza18);
        mockFlussoV18.getInformazioniContoEvidenza().add(informazioniContoEvidenza18);

        mockFile = new File("testFile.xml");

        mockFlussoV18NoIufNoIuv = new FlussoGiornaleDiCassa();
        mockFlussoV18NoIufNoIuv.getEsercizio().add(2024);
        InformazioniContoEvidenza informazioniContoEvidenza171NoIufNoIuv = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza18NoIufNoIuv = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        movimentoContoEvidenza18NoIufNoIuv.setCausale("ACCREDITI VARI");
        movimentoContoEvidenza18NoIufNoIuv.setNumeroBollettaQuietanza(new BigInteger("999"));
        informazioniContoEvidenza171NoIufNoIuv.getMovimentoContoEvidenzas().add(movimentoContoEvidenza18NoIufNoIuv);
        mockFlussoV18NoIufNoIuv.getInformazioniContoEvidenza().add(informazioniContoEvidenza171NoIufNoIuv);

        mockFlussoV18NoEsercizio = new FlussoGiornaleDiCassa();
        InformazioniContoEvidenza informazioniContoEvidenza18NoEsercizio = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza18NoEsercizio = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        informazioniContoEvidenza18NoEsercizio.getMovimentoContoEvidenzas().add(movimentoContoEvidenza18NoEsercizio);
        mockFlussoV18NoEsercizio.getInformazioniContoEvidenza().add(informazioniContoEvidenza18NoEsercizio);

    }

    @Test
    void validateDataV18() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV18;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService18.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void validateDataV18NoIufNoIuv() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV18NoIufNoIuv;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService18.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(9, result.size());

        assertEquals("Tipo movimento field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo documento field is not valorized but it is required", result.get(1).getErrorMessage());
        assertEquals("Tipo operazione field is not valorized but it is required", result.get(2).getErrorMessage());
    }


    @Test
    void validateDataV18NoEsercizio() {
        // Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV18NoEsercizio;

        // When
        List<TreasuryErrorDTO> result = treasuryValidatorService18.validateData(flussoGiornaleDiCassa, mockFile.getName());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(11, result.size());

        assertEquals("Esercizio field is not valorized but it is required", result.get(0).getErrorMessage());
        assertEquals("Tipo movimento field is not valorized but it is required", result.get(1).getErrorMessage());
    }

    @Test
    void whenValidatePageSizeThenKo() {
        //Given
        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mockFlussoV18;

        //When
        boolean res = treasuryValidatorService18.validatePageSize(flussoGiornaleDiCassa, 6);

        //Then
        assertFalse(res);
    }
}
