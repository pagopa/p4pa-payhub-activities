package it.gov.pagopa.payhub.activities.service.treasury.opi161;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza;
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

class TreasuryMapperOpi161ServiceTest {

    public static final String LAST_NAME_CLIENTE = "Last name cliente";
    public static final String ADDRESS_CLIENTE = "Address cliente";
    public static final String POSTAL_CODE = "01234";
    public static final String CITY = "City";
    public static final String FISCAL_CODE = "AAABBB12A34A567Z";
    public static final String VAT_NUMBER = "IT09876543123";
    private TreasuryMapperOpi161Service treasuryMapperService;


    @BeforeEach
    void setUp() {
        treasuryMapperService = new TreasuryMapperOpi161Service();
    }

    @Test
    void testApply_givenValidInput_whenMapping_thenCorrectResult() throws Exception {

        FlussoGiornaleDiCassa flussoGiornaleDiCassa = new FlussoGiornaleDiCassa();
        InformazioniContoEvidenza informazioniContoEvidenza = new InformazioniContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza = new InformazioniContoEvidenza.MovimentoContoEvidenza();
        InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = new InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente();
        InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare sospesoDaRegolarizzare = new InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare();

        flussoGiornaleDiCassa.getEsercizio().add(2023);

        movimentoContoEvidenza.setTipoMovimento("ENTRATA");
        movimentoContoEvidenza.setTipoDocumento("SOSPESO ENTRATA");
        movimentoContoEvidenza.setTipoOperazione("ESEGUITO");
        movimentoContoEvidenza.setNumeroBollettaQuietanza(BigInteger.ONE);
        movimentoContoEvidenza.setImporto(BigDecimal.TEN);
        movimentoContoEvidenza.setDataMovimento(toXMLGregorianCalendar(new GregorianCalendar(2023, Calendar.JANUARY, 1)));
        movimentoContoEvidenza.setDataValutaEnte(toXMLGregorianCalendar(new GregorianCalendar(2024, Calendar.JANUARY, 1)));
        sospesoDaRegolarizzare.setDataEffettivaSospeso(toXMLGregorianCalendar(new GregorianCalendar(2024, Calendar.JANUARY, 1)));
        sospesoDaRegolarizzare.setCodiceGestionaleProvvisorio("ABC");
        movimentoContoEvidenza.setSospesoDaRegolarizzare(sospesoDaRegolarizzare);
        movimentoContoEvidenza.setCausale("ACCREDITI VARI LGPE-RIVERSAMENTO/URI/2024-12-15 IUV_TEST_RFS12345678901234567891234567890");
        movimentoContoEvidenza.setEndToEndId("e2eId");
        movimentoContoEvidenza.setCliente(cliente);
        cliente.setAnagraficaCliente(LAST_NAME_CLIENTE);
        cliente.setIndirizzoCliente(ADDRESS_CLIENTE);
        cliente.setCapCliente(POSTAL_CODE);
        cliente.setLocalitaCliente(CITY);
        cliente.setCodiceFiscaleCliente(FISCAL_CODE);
        cliente.setPartitaIvaCliente(VAT_NUMBER);

        informazioniContoEvidenza.getMovimentoContoEvidenzas().add(movimentoContoEvidenza);
        flussoGiornaleDiCassa.getInformazioniContoEvidenza().add(informazioniContoEvidenza);

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
        TestUtils.checkNotNullFields(treasuryDTO,
                "treasuryId","updateOperatorExternalId", "iuv","accountCode","domainIdCode",
                "transactionTypeCode","remittanceCode","documentYear","sealCode",
                "pspFirstName","abiCode","cabCode","ibanCode","accountRegistryCode",
                "provisionalAe","provisionalCode","accountTypeCode","processCode",
                "executionPgCode","transferPgCode","processPgNumber","regularized",
                "links"
        );

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
