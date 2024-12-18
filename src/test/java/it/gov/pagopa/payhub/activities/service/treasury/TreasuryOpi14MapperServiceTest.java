package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.service.cipher.DataCipherService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class TreasuryOpi14MapperServiceTest {
	@Mock
	private DataCipherService dataCipherService;

	@InjectMocks
	private TreasuryOpi14MapperService treasuryOpi14MapperService;

	private static final String CLIENTE="cliente";

	private static final byte[] CLIENTE_HASH = new byte[]{1,2,3,4};

	@Test
	void testMapper() throws DatatypeConfigurationException {
		// Given
		FlussoGiornaleDiCassa flussoGiornaleDiCassa = createFlussoGiornaleDiCassa();
		IngestionFlowFileDTO ingestionFlowFileDTO = createIngestionFlowFileDTO();
		Mockito.when(dataCipherService.encryptObj(CLIENTE)).thenReturn(CLIENTE_HASH);

		// When
		Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> result = treasuryOpi14MapperService.apply(flussoGiornaleDiCassa, ingestionFlowFileDTO);

		List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> pairs=result.get(TreasuryOpi14MapperService.INSERT);

		TreasuryDTO firstDTO = pairs.get(0).getLeft();
		// Then
		assertEquals(2, result.size());
		assertEquals(1, pairs.size());
		assertEquals(2L, firstDTO.getIngestionFlowFileId());
		assertEquals("2024", firstDTO.getBillYear());
		assertEquals("543", firstDTO.getBillCode());
		assertEquals(new BigDecimal(756), firstDTO.getBillIpNumber());
		assertEquals("5", firstDTO.getDocumentCode());
		assertEquals(1L, firstDTO.getOrganizationId());
		assertEquals("2022-09-28CIPBITMM-N01080020792913", firstDTO.getFlowIdentifierCode());
        assertNull(firstDTO.getIuv());
		assertEquals("cod123", firstDTO.getManagementProvisionalCode());
		assertEquals("endToEnd1", firstDTO.getEndToEndId());
		assertEquals(CLIENTE_HASH, firstDTO.getLastNameHash());
		assertNotNull(firstDTO.getBillDate());
		assertNotNull(firstDTO.getReceptionDate());
		assertNotNull(firstDTO.getRegionValueDate());
		assertNotNull(firstDTO.getCreationDate());
		assertNotNull(firstDTO.getLastUpdateDate());
		assertNotNull(firstDTO.getActualSuspensionDate());
	}

	private static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar gCalendar) throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
	}

	private FlussoGiornaleDiCassa createFlussoGiornaleDiCassa() throws DatatypeConfigurationException {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2024, Calendar.DECEMBER, 25);
		GregorianCalendar gregorianCalendar2 = new GregorianCalendar(2024, Calendar.DECEMBER, 10);
		GregorianCalendar gregorianCalendar3 = new GregorianCalendar(2024, Calendar.DECEMBER, 14);

		FlussoGiornaleDiCassa flusso = new FlussoGiornaleDiCassa();
		flusso.getEsercizio().add(2024);
		InformazioniContoEvidenza infoContoEvidenza = new InformazioniContoEvidenza();
		InformazioniContoEvidenza.MovimentoContoEvidenza movimento = new InformazioniContoEvidenza.MovimentoContoEvidenza();
		movimento.setTipoMovimento("ENTRATA");
		movimento.setTipoDocumento("SOSPESO ENTRATA");
		movimento.setTipoOperazione("ESEGUITO");
		movimento.setCausale("LGPE-RIVERSAMENTO/URI/2022-09-28CIPBITMM-N01080020792913");
		movimento.setNumeroBollettaQuietanza(new BigInteger("543"));
		movimento.setImporto(new BigDecimal(756));
		movimento.setNumeroDocumento(5L);
		movimento.setDataMovimento(toXMLGregorianCalendar(gregorianCalendar));
		movimento.setDataValutaEnte(toXMLGregorianCalendar(gregorianCalendar2));
		InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare sospesoDaRegolarizzare= new InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare();
		sospesoDaRegolarizzare.setDataEffettivaSospeso(toXMLGregorianCalendar(gregorianCalendar3));
		sospesoDaRegolarizzare.setCodiceGestionaleProvvisorio("cod123");
		movimento.setSospesoDaRegolarizzare(sospesoDaRegolarizzare);
		movimento.setEndToEndId("endToEnd1");
		InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = new InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente();
		cliente.setAnagraficaCliente(CLIENTE);
		movimento.setCliente(cliente);
		movimento.setSospesoDaRegolarizzare(sospesoDaRegolarizzare);
		infoContoEvidenza.getMovimentoContoEvidenzas().add(movimento);
		flusso.getInformazioniContoEvidenza().add(infoContoEvidenza);
		return flusso;
	}

	private IngestionFlowFileDTO createIngestionFlowFileDTO() {
		IngestionFlowFileDTO dto = new IngestionFlowFileDTO();
		OrganizationDTO orgDTO = new OrganizationDTO();
		orgDTO.setOrgId(1L);
		dto.setOrg(orgDTO);
		dto.setIngestionFlowFileId(2L);
		return dto;
	}

}