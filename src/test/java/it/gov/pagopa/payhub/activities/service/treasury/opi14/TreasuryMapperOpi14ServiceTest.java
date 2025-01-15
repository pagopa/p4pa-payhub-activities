package it.gov.pagopa.payhub.activities.service.treasury.opi14;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreasuryMapperOpi14ServiceTest {

    public static final String LAST_NAME_CLIENTE = "Last name cliente";
    public static final String ADDRESS_CLIENTE = "Address cliente";
    public static final String POSTAL_CODE = "01234";
    public static final String CITY = "City";
    public static final String FISCAL_CODE = "AAABBB12A34A567Z";
    public static final String VAT_NUMBER = "IT09876543123";
    private TreasuryMapperOpi14Service treasuryMapperService;


    @BeforeEach
    void setUp() {
        treasuryMapperService = new TreasuryMapperOpi14Service();
    }

    @Test
    void testApply_givenValidInput_whenMapping_thenCorrectResult() throws Exception {

        FlussoGiornaleDiCassa flussoGiornaleDiCassa = mock(FlussoGiornaleDiCassa.class);
        InformazioniContoEvidenza informazioniContoEvidenza = mock(InformazioniContoEvidenza.class);
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza = mock(InformazioniContoEvidenza.MovimentoContoEvidenza.class);
        InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = mock(InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente.class);
        InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare sospesoDaRegolarizzare = mock(InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare.class);

        when(flussoGiornaleDiCassa.getInformazioniContoEvidenza()).thenReturn(List.of(informazioniContoEvidenza));
        when(flussoGiornaleDiCassa.getEsercizio()).thenReturn(List.of(2023));
        when(informazioniContoEvidenza.getMovimentoContoEvidenzas()).thenReturn(List.of(movimentoContoEvidenza));

        when(movimentoContoEvidenza.getTipoMovimento()).thenReturn("ENTRATA");
        when(movimentoContoEvidenza.getTipoDocumento()).thenReturn("SOSPESO ENTRATA");
        when(movimentoContoEvidenza.getTipoOperazione()).thenReturn("ESEGUITO");
        when(movimentoContoEvidenza.getNumeroBollettaQuietanza()).thenReturn(BigInteger.ONE);
        when(movimentoContoEvidenza.getImporto()).thenReturn(BigDecimal.TEN);
        when(movimentoContoEvidenza.getDataMovimento()).thenReturn(toXMLGregorianCalendar(new GregorianCalendar(2023, Calendar.JANUARY, 1)));
        when(movimentoContoEvidenza.getDataValutaEnte()).thenReturn(toXMLGregorianCalendar(new GregorianCalendar(2024, Calendar.JANUARY, 1)));
        when(movimentoContoEvidenza.getSospesoDaRegolarizzare()).thenReturn(sospesoDaRegolarizzare);
        when(sospesoDaRegolarizzare.getDataEffettivaSospeso()).thenReturn(toXMLGregorianCalendar(new GregorianCalendar(2024, Calendar.JANUARY, 1)));
        when(movimentoContoEvidenza.getCausale()).thenReturn("CAUSALE123");
        when(movimentoContoEvidenza.getCliente()).thenReturn(cliente);
        when(cliente.getAnagraficaCliente()).thenReturn(LAST_NAME_CLIENTE);
        when(cliente.getIndirizzoCliente()).thenReturn(ADDRESS_CLIENTE);
        when(cliente.getCapCliente()).thenReturn(POSTAL_CODE);
        when(cliente.getLocalitaCliente()).thenReturn(CITY);
        when(cliente.getCodiceFiscaleCliente()).thenReturn(FISCAL_CODE);
        when(cliente.getPartitaIvaCliente()).thenReturn(VAT_NUMBER);

        IngestionFlowFileDTO ingestionFlowFileDTO = createIngestionFlowFileDTO();
        Map<TreasuryOperationEnum, List<Treasury>> result = treasuryMapperService.apply(flussoGiornaleDiCassa, ingestionFlowFileDTO);

        assertNotNull(result);
        assertTrue(result.containsKey(TreasuryOperationEnum.INSERT));
        assertFalse(result.get(TreasuryOperationEnum.INSERT).isEmpty());

        List<Treasury> treasuryDTOList = result.get(TreasuryOperationEnum.INSERT);
        assertNotNull(treasuryDTOList);

        Treasury treasuryDTO = treasuryDTOList.getFirst();
        assertEquals("2023", treasuryDTO.getBillYear());
        assertEquals("1", treasuryDTO.getBillCode());
        assertEquals(1000L, treasuryDTO.getBillAmountCents());
        assertEquals(LAST_NAME_CLIENTE, treasuryDTO.getPspLastName());
        assertEquals(ADDRESS_CLIENTE, treasuryDTO.getPspAddress());
        assertEquals(POSTAL_CODE, treasuryDTO.getPspPostalCode());
        assertEquals(CITY, treasuryDTO.getPspCity());
        assertEquals(FISCAL_CODE, treasuryDTO.getPspFiscalCode());
        assertEquals(VAT_NUMBER, treasuryDTO.getPspVatNumber());

    }

    private IngestionFlowFileDTO createIngestionFlowFileDTO() {
        IngestionFlowFileDTO dto = new IngestionFlowFileDTO();
        Organization orgDTO = new Organization();
        orgDTO.setOrganizationId(1L);
        dto.setOrg(orgDTO);
        dto.setIngestionFlowFileId(2L);
        return dto;
    }

    private static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar gCalendar) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
    }
}
