package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreasuryMapperServiceTest {

    public static final String LAST_NAME_CLIENTE = "Last name cliente";
    public static final String ADDRESS_CLIENTE = "Address cliente";
    public static final String POSTAL_CODE = "01234";
    public static final String CITY = "City";
    public static final String FISCAL_CODE = "AAABBB12A34A567Z";
    public static final String VAT_NUMBER = "IT09876543123";
    private TreasuryMapperService treasuryMapperService;


    @BeforeEach
    void setUp() {
        treasuryMapperService = new TreasuryMapperService();
    }

    @Test
    void testApply_givenValidInput_whenMapping_thenCorrectResult() throws Exception {

        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mock(FlussoGiornaleDiCassa.class);
        InformazioniContoEvidenza informazioniContoEvidenza = mock(InformazioniContoEvidenza.class);
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza = mock(InformazioniContoEvidenza.MovimentoContoEvidenza.class);
        InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = mock(InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente.class);

        when(flussoGiornaleDiCassa.getInformazioniContoEvidenza()).thenReturn(List.of(informazioniContoEvidenza));
        when(flussoGiornaleDiCassa.getEsercizio()).thenReturn(List.of(2023));
        when(informazioniContoEvidenza.getMovimentoContoEvidenzas()).thenReturn(List.of(movimentoContoEvidenza));

        when(movimentoContoEvidenza.getTipoMovimento()).thenReturn("ENTRATA");
        when(movimentoContoEvidenza.getTipoDocumento()).thenReturn("SOSPESO ENTRATA");
        when(movimentoContoEvidenza.getTipoOperazione()).thenReturn("ESEGUITO");
        when(movimentoContoEvidenza.getNumeroBollettaQuietanza()).thenReturn(BigInteger.ONE);
        when(movimentoContoEvidenza.getImporto()).thenReturn(BigDecimal.TEN);
        when(movimentoContoEvidenza.getDataMovimento()).thenReturn(toXMLGregorianCalendar(new GregorianCalendar(2023, Calendar.JANUARY, 1)));
        when(movimentoContoEvidenza.getCausale()).thenReturn("CAUSALE123");
        when(movimentoContoEvidenza.getCliente()).thenReturn(cliente);
        when(cliente.getAnagraficaCliente()).thenReturn(LAST_NAME_CLIENTE);
        when(cliente.getIndirizzoCliente()).thenReturn(ADDRESS_CLIENTE);
        when(cliente.getCapCliente()).thenReturn(POSTAL_CODE);
        when(cliente.getLocalitaCliente()).thenReturn(CITY);
        when(cliente.getCodiceFiscaleCliente()).thenReturn(FISCAL_CODE);
        when(cliente.getPartitaIvaCliente()).thenReturn(VAT_NUMBER);






        IngestionFlowFileDTO ingestionFlowFileDTO = createIngestionFlowFileDTO();
        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> result =
                treasuryMapperService.apply(flussoGiornaleDiCassa, ingestionFlowFileDTO);

        assertNotNull(result);
        assertTrue(result.containsKey(TreasuryMapperService.INSERT));
        assertFalse(result.get(TreasuryMapperService.INSERT).isEmpty());

        Pair<TreasuryDTO, FlussoTesoreriaPIIDTO> mappedPair = result.get(TreasuryMapperService.INSERT).get(0);
        assertNotNull(mappedPair);

        TreasuryDTO treasuryDTO = mappedPair.getLeft();
        assertEquals("2023", treasuryDTO.getBillYear());
        assertEquals("1", treasuryDTO.getBillCode());
        assertEquals(BigDecimal.TEN, treasuryDTO.getBillIpNumber());
        assertEquals(LAST_NAME_CLIENTE, treasuryDTO.getLastName());
        assertEquals(ADDRESS_CLIENTE, treasuryDTO.getAddress());
        assertEquals(POSTAL_CODE, treasuryDTO.getPostalCode());
        assertEquals(CITY, treasuryDTO.getCity());
        assertEquals(FISCAL_CODE, treasuryDTO.getFiscalCode());
        assertEquals(VAT_NUMBER, treasuryDTO.getVatNumber());

        FlussoTesoreriaPIIDTO flussoTesoreriaPIIDTO = mappedPair.getRight();
        assertNotNull(flussoTesoreriaPIIDTO);
        assertEquals("CAUSALE123", flussoTesoreriaPIIDTO.getDeCausale());
    }

    private IngestionFlowFileDTO createIngestionFlowFileDTO() {
        IngestionFlowFileDTO dto = new IngestionFlowFileDTO();
        OrganizationDTO orgDTO = new OrganizationDTO();
        orgDTO.setOrgId(1L);
        dto.setOrg(orgDTO);
        dto.setIngestionFlowFileId(2L);
        return dto;
    }

    private static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar gCalendar) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
    }
}
