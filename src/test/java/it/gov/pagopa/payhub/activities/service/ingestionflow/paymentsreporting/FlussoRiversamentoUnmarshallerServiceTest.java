package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.payhub.activities.service.files.XMLUnmarshallerService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlussoRiversamentoUnmarshallerServiceTest {
	private static final String XML_CONTENT = """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<FlussoRiversamento xmlns="http://www.digitpa.gov.it/schemas/2011/Pagamenti/">
			    <versioneOggetto>1.0</versioneOggetto>
			    <identificativoFlusso>2024-04-07ABI03062-315V900103811327</identificativoFlusso>
			    <revisioneFlusso>1</revisioneFlusso>
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
	private FlussoRiversamentoUnmarshallerService handler;
	private XMLUnmarshallerService xmlUnmarshallerService;

	@TempDir
	File tempDir;

	@BeforeEach
	void setUp() {
		xmlUnmarshallerService = new XMLUnmarshallerService();
		resource = new ClassPathResource("xsd/FlussoRiversamento.xsd");
		handler = new FlussoRiversamentoUnmarshallerService(resource, xmlUnmarshallerService);
	}

	@Test
	void testHandleValidXml() throws Exception {
		// given
		File xmlFile = new File(tempDir, "testFlussoRiversamento.xml");
		try (FileWriter writer = new FileWriter(xmlFile)) {
			writer.write(XML_CONTENT);
		}
		//when
		CtFlussoRiversamento result = handler.unmarshal(xmlFile);

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
		assertThrows(InvalidValueException.class,
			() -> handler.unmarshal(xmlFile), "Error while parsing file"
		);
	}

	@Test
	void testJAXBExceptionInConstructor() {
		try(MockedStatic<JAXBContext> mockedStaticJAXBContext = Mockito.mockStatic(JAXBContext.class)) {
			mockedStaticJAXBContext.when(() -> JAXBContext.newInstance(CtFlussoRiversamento.class))
					.thenThrow(new JAXBException("Simulated JAXBException"));
			assertThrows(IllegalStateException.class, () -> new FlussoRiversamentoUnmarshallerService(resource, null));
		}
	}

	@Test
	void testIOExceptionInConstructor() throws Exception {
		// given
		Resource mockResource = mock(Resource.class);
		when(mockResource.getURL()).thenThrow(new IOException("Simulated IOException"));

		// when then
		assertThrows(IllegalStateException.class, () -> new FlussoRiversamentoUnmarshallerService(mockResource, null));
	}
}
