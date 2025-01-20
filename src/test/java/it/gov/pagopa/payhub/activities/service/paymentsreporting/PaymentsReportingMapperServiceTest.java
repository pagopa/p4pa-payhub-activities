package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.*;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.GregorianCalendar;
import java.util.List;

import static it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivoco.B;
import static it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivocoPersG.G;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentsReportingMapperServiceTest {
	private final PaymentsReportingMapperService mapper = new PaymentsReportingMapperService();

	@Test
	void testMap2PaymentsReportings() throws DatatypeConfigurationException {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2024, GregorianCalendar.DECEMBER, 25);

		// Given
		CtFlussoRiversamento ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("flow123");

		ctFlussoRiversamento.setIdentificativoUnivocoRegolamento("reg123");
		ctFlussoRiversamento.setCodiceBicBancaDiRiversamento("BIC123");
		ctFlussoRiversamento.setNumeroTotalePagamenti(BigDecimal.valueOf(1L));
		ctFlussoRiversamento.setImportoTotalePagamenti(BigDecimal.valueOf(1_000.50D));

		ctFlussoRiversamento.setDataRegolamento(toXMLGregorianCalendar(gregorianCalendar));
		ctFlussoRiversamento.setDataOraFlusso(toXMLGregorianCalendar(gregorianCalendar));

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
		ingestionFlowFileDTO.setOrg(Organization.builder().organizationId(1L).build());
		ingestionFlowFileDTO.setIngestionFlowFileId(1L);
		ingestionFlowFileDTO.setCreationDate(Instant.now());

		CtDatiSingoliPagamenti singlePayment = new CtDatiSingoliPagamenti();
		singlePayment.setIdentificativoUnivocoVersamento("vers123");
		singlePayment.setIdentificativoUnivocoRiscossione("ris123");
		singlePayment.setIndiceDatiSingoloPagamento(1);
		singlePayment.setSingoloImportoPagato(BigDecimal.valueOf(200.00D));
		singlePayment.setCodiceEsitoSingoloPagamento("OK");
		singlePayment.setDataEsitoSingoloPagamento(toXMLGregorianCalendar(gregorianCalendar));

		ctFlussoRiversamento.getDatiSingoliPagamenti().add(singlePayment);

		// When
		List<PaymentsReporting> result = mapper.map2PaymentsReportings(ctFlussoRiversamento, ingestionFlowFileDTO);

		PaymentsReporting firstDTO = result.getFirst();
		// Then
		assertEquals(1, result.size());
		assertEquals("flow123", firstDTO.getIuf());
		assertEquals("PSP Mittente", firstDTO.getSenderPspName());
		assertEquals("Org Ricevente", firstDTO.getReceiverOrganizationName());
		assertEquals(1L, firstDTO.getTotalPayments());
		assertEquals(100_050L, firstDTO.getTotalAmountCents());
		assertEquals("vers123", firstDTO.getIuv());
		assertEquals("ris123", firstDTO.getIur());
		assertEquals(1, firstDTO.getTransferIndex());
		assertEquals(20_000L, firstDTO.getAmountPaidCents());
		assertEquals("OK", firstDTO.getPaymentOutcomeCode());
		assertNotNull(firstDTO.getPayDate());

		TestUtils.checkNotNullFields(firstDTO, "paymentsReportingId", "updateOperatorExternalId", "links");
	}

	@Test
	void testMap2TransferSemanticKeyDto() {
		// Given
		PaymentsReporting paymentsReporting = PaymentsReporting.builder()
			.organizationId(1L)
			.iuv("iuv123")
			.iur("iur123")
			.transferIndex(1)
			.build();

		// When
		TransferSemanticKeyDTO result = mapper.map2TransferSemanticKeyDto(paymentsReporting);

		// Then
		assertEquals(1L, result.getOrgId());
		assertEquals("iuv123", result.getIuv());
		assertEquals("iur123", result.getIur());
		assertEquals(1, result.getTransferIndex());

		TestUtils.checkNotNullFields(result);
	}

	private static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar gCalendar) throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
	}
}