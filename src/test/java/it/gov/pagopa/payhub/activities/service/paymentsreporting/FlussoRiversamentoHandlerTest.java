package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.XMLUnmarshallerService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlussoRiversamentoHandlerTest {
	private static final String XML_CONTENT = """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<FlussoRiversamento xmlns="http://www.digitpa.gov.it/schemas/2011/Pagamenti/">
			    <versioneOggetto>1.0</versioneOggetto>
			    <identificativoFlusso>2024-04-07ABI03062-315V900103811327</identificativoFlusso>
			    <dataOraFlusso>2024-12-01T12:00:00</dataOraFlusso>
			    <identificativoUnivocoRegolamento>Bonifico SEPA-03062-315V9</identificativoUnivocoRegolamento>
			    <dataRegolamento>2024-12-01</dataRegolamento>
			    <istitutoMittente>
			        <identificativoUnivocoMittente>
			            <tipoIdentificativoUnivoco>B</tipoIdentificativoUnivoco>
			            <codiceIdentificativoUnivoco>ABI00000</codiceIdentificativoUnivoco>
			        </identificativoUnivocoMittente>
			        <denominazioneMittente>BANCA</denominazioneMittente>
			    </istitutoMittente>
			    <istitutoRicevente>
			        <identificativoUnivocoRicevente>
			            <tipoIdentificativoUnivoco>G</tipoIdentificativoUnivoco>
			            <codiceIdentificativoUnivoco>00625230271</codiceIdentificativoUnivoco>
			        </identificativoUnivocoRicevente>
			        <denominazioneRicevente>COMUNE DI VENEZIA</denominazioneRicevente>
			    </istitutoRicevente>
			    <numeroTotalePagamenti>2</numeroTotalePagamenti>
			    <importoTotalePagamenti>300.00</importoTotalePagamenti>
			    <datiSingoliPagamenti>
			        <identificativoUnivocoVersamento>01000000001122011</identificativoUnivocoVersamento>
			        <identificativoUnivocoRiscossione>329181f45b5e4889825fe3d4e6e9418a</identificativoUnivocoRiscossione>
			        <indiceDatiSingoloPagamento>1</indiceDatiSingoloPagamento>
			        <singoloImportoPagato>150.00</singoloImportoPagato>
			        <codiceEsitoSingoloPagamento>9</codiceEsitoSingoloPagamento>
			        <dataEsitoSingoloPagamento>2024-12-01</dataEsitoSingoloPagamento>
			    </datiSingoliPagamenti>
			    <datiSingoliPagamenti>
			        <identificativoUnivocoVersamento>01000000001122011</identificativoUnivocoVersamento>
			        <identificativoUnivocoRiscossione>329181f45b5e4889825fe3d4e6e9418a</identificativoUnivocoRiscossione>
			        <indiceDatiSingoloPagamento>1</indiceDatiSingoloPagamento>
			        <singoloImportoPagato>150.00</singoloImportoPagato>
			        <codiceEsitoSingoloPagamento>9</codiceEsitoSingoloPagamento>
			        <dataEsitoSingoloPagamento>2024-12-01</dataEsitoSingoloPagamento>
			    </datiSingoliPagamenti>
			</FlussoRiversamento>
                """;

	private Resource resource;
	private FlussoRiversamentoHandler handler;
	private XMLUnmarshallerService xmlUnmarshallerService;

	@TempDir
	File tempDir;

	@BeforeEach
	void setUp() {
		xmlUnmarshallerService = new XMLUnmarshallerService();
		resource = new ClassPathResource("xsd/FlussoRiversamento.xsd");
		handler = new FlussoRiversamentoHandler(resource, xmlUnmarshallerService);
	}

	@Test
	void testHandleValidXml() throws Exception {
		// given
		File xmlFile = new File(tempDir, "testFlussoRiversamento.xml");
		try (FileWriter writer = new FileWriter(xmlFile)) {
			writer.write(XML_CONTENT);
		}
		//when
		CtFlussoRiversamento result = handler.handle(xmlFile);

		// then
		assertNotNull(result);
		assertEquals("2024-04-07ABI03062-315V900103811327", result.getIdentificativoFlusso());
		assertEquals(BigDecimal.valueOf(2L), result.getNumeroTotalePagamenti());
	}

	@Test
	void testHandleInvalidXml() throws Exception {
		// given
		File xmlFile = new File(tempDir, "invalid.xml");
		try (FileWriter writer = new FileWriter(xmlFile)) {
			writer.write("<testObject><invalidElement>Invalid</invalidElement></testObject>");
		}

		// when then
		assertThrows(ActivitiesException.class,
			() -> handler.handle(xmlFile), "Error while parsing file"
		);
	}

	@Test
	void testJAXBExceptionInConstructor() {
		Mockito.mockStatic(JAXBContext.class).when(() -> JAXBContext.newInstance(CtFlussoRiversamento.class))
			.thenThrow(new JAXBException("Simulated JAXBException"));
		assertThrows(ActivitiesException.class, () -> new FlussoRiversamentoHandler(resource, null));
	}

	@Test
	void testIOExceptionInConstructor() throws Exception {
		// Mock the Resource to simulate an exception during URL retrieval
		Resource mockResource = mock(Resource.class);
		when(mockResource.getURL()).thenThrow(new IOException("Simulated IOException"));

		// Assert that ActivitiesException is thrown
		assertThrows(ActivitiesException.class, () -> new FlussoRiversamentoHandler(mockResource, null));
	}
}
