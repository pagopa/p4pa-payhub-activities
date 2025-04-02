package it.gov.pagopa.payhub.activities.service.files;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class XMLUnmarshallerServiceTest {
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

	private JAXBContext jaxbContext;
	private Schema schema;
	private XMLUnmarshallerService service;

	@TempDir
	File tempDir;

	@BeforeEach
	void setUp() throws JAXBException {
		service = new XMLUnmarshallerService();
		jaxbContext = JAXBContext.newInstance(CtFlussoRiversamento.class);

		try {
			URL xsdUrl = getClass().getResource("/xsd/FlussoRiversamento.xsd");
			if (xsdUrl != null) {
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				schema = schemaFactory.newSchema(xsdUrl);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error initializing Schema for testing", e);
		}
	}

	@Test
	void unmarshaller_validXmlWithSchemaFromResources_shouldReturnCtFlussoRiversamento() throws Exception {
		//given
		File xmlFile = new File(tempDir, "testFlussoRiversamento.xml");

		try (FileWriter xmlWriter = new FileWriter(xmlFile)) {
			xmlWriter.write(XML_CONTENT);
		}

		// when
		CtFlussoRiversamento result = service.unmarshal(xmlFile, CtFlussoRiversamento.class, jaxbContext, schema);

		// then
		assertNotNull(result);
		assertEquals("2024-04-07ABI03062-315V900103811327", result.getIdentificativoFlusso());
		assertEquals("BANCA", result.getIstitutoMittente().getDenominazioneMittente());
		assertEquals("COMUNE DI VENEZIA", result.getIstitutoRicevente().getDenominazioneRicevente());
		assertEquals(2, result.getDatiSingoliPagamenti().size());
		assertEquals("01000000001122011", result.getDatiSingoliPagamenti().get(0).getIdentificativoUnivocoVersamento());
	}

	@Test
	void unmarshaller_invalidXml_shouldThrowException() throws Exception {
		// given
		File xmlFile = new File(tempDir, "invalid.xml");
		String invalidXmlContent = "<testObject><invalidElement>Invalid</invalidElement></testObject>";
		Files.writeString(xmlFile.toPath(), invalidXmlContent);

		// when then
		assertThrows(InvalidValueException.class, () ->
				service.unmarshal(xmlFile, CtFlussoRiversamento.class, jaxbContext, schema),
				"Error while parsing file"
		);
	}

	@Test
	void unmarshaller_validXmlWithoutSchema_successfulUnmarshaller() throws Exception {
		//given
		File xmlFile = new File(tempDir, "testFlussoRiversamento.xml");

		try (FileWriter xmlWriter = new FileWriter(xmlFile)) {
			xmlWriter.write(XML_CONTENT);
		}

		// when then
		assertDoesNotThrow(() -> service.unmarshal(xmlFile, CtFlussoRiversamento.class, jaxbContext,null));
	}
}
