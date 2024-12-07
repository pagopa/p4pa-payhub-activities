package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.*;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import static it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivoco.B;
import static it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivocoPersG.G;
import static org.junit.jupiter.api.Assertions.*;

class PaymentsReportingMapperServiceTest {
	private PaymentsReportingMapperService mapper = new PaymentsReportingMapperService();

	@Test
	void testMapper() throws DatatypeConfigurationException {
		// Given
		CtFlussoRiversamento ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("flow123");

		ctFlussoRiversamento.setIdentificativoUnivocoRegolamento("reg123");
		ctFlussoRiversamento.setCodiceBicBancaDiRiversamento("BIC123");
		ctFlussoRiversamento.setNumeroTotalePagamenti(BigDecimal.valueOf(100L));
		ctFlussoRiversamento.setImportoTotalePagamenti(BigDecimal.valueOf(1000.50d));
		ctFlussoRiversamento.getDatiSingoliPagamenti().addAll(List.of(new CtDatiSingoliPagamenti()));

		ctFlussoRiversamento.setDataRegolamento(toXMLGregorianCalendar(new GregorianCalendar()));
		ctFlussoRiversamento.setDataOraFlusso(toXMLGregorianCalendar(new GregorianCalendar()));

		CtIstitutoMittente istitutoMittente = new CtIstitutoMittente();
		istitutoMittente.setDenominazioneMittente("PSP Mittente");
		CtIdentificativoUnivoco identificativoMittente = new CtIdentificativoUnivoco();
		identificativoMittente.setCodiceIdentificativoUnivoco("12345");
		identificativoMittente.setTipoIdentificativoUnivoco(B);
		istitutoMittente.setIdentificativoUnivocoMittente(identificativoMittente);
		ctFlussoRiversamento.setIstitutoMittente(istitutoMittente);

		CtIstitutoRicevente istitutoRicevente = new CtIstitutoRicevente();
		istitutoRicevente.setDenominazioneRicevente("Org Ricevente");
		CtIdentificativoUnivocoPersonaG identificativoRicevente = new CtIdentificativoUnivocoPersonaG();
		identificativoRicevente.setCodiceIdentificativoUnivoco("54321");
		identificativoRicevente.setTipoIdentificativoUnivoco(G);
		istitutoRicevente.setIdentificativoUnivocoRicevente(identificativoRicevente);
		ctFlussoRiversamento.setIstitutoRicevente(istitutoRicevente);

		IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
		ingestionFlowFileDTO.setOrg(new OrganizationDTO());

		CtDatiSingoliPagamenti singlePayment = new CtDatiSingoliPagamenti();
		singlePayment.setIdentificativoUnivocoVersamento("vers123");
		singlePayment.setIdentificativoUnivocoRiscossione("ris123");
		singlePayment.setIndiceDatiSingoloPagamento(1);
		singlePayment.setSingoloImportoPagato(BigDecimal.valueOf(200.0D));
		singlePayment.setCodiceEsitoSingoloPagamento("OK");
		singlePayment.setDataEsitoSingoloPagamento(toXMLGregorianCalendar(new GregorianCalendar()));
		
		ctFlussoRiversamento.getDatiSingoliPagamenti().add(singlePayment);

		// When
		List<PaymentsReportingDTO> result = mapper.mapToDtoList(ctFlussoRiversamento, ingestionFlowFileDTO);

		// Then
		assertEquals(1, result.size());
		assertEquals("flow123", result.get(0).getFlowIdentifierCode());
		assertEquals("PSP Mittente", result.get(0).getSenderPspName());
		assertEquals("Org Ricevente", result.get(0).getReceiverOrganizationName());
		assertEquals(100L, result.get(0).getSumPayments().longValue());
		assertEquals(1000.50d, result.get(0).getAmountPaid().doubleValue());
		assertEquals("vers123", result.get(0).getCreditorReferenceId());
		assertEquals("ris123", result.get(0).getRegulationId());
		assertEquals(1, result.get(0).getTransferIndex());
		assertEquals(200.00D, result.get(0).getAmountPaid().doubleValue());
		assertEquals("OK", result.get(0).getPaymentOutcomeCode());
		assertNotNull(result.get(0).getPayDate());
	}

	private static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar gCalendar) throws DatatypeConfigurationException {
		return  DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
	}
}