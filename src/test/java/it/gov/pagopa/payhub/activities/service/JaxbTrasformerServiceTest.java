package it.gov.pagopa.payhub.activities.service;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class JaxbTrasformerServiceTest {

	private final URL xsdUrl = getClass().getResource("/xsd/FlussoRiversamento.xsd");
	private final JaxbTrasformerService service = new JaxbTrasformerService();

	@TempDir
	File tempDir;

	@Test
	void unmarshaller_validXmlWithSchemaFromResources_shouldReturnCtFlussoRiversamento() throws Exception {
		assertNotNull(xsdUrl, "XSD file not found in resources");
		//given
		File xmlFile = new File(tempDir, "testFlussoRiversamento.xml");

		String xmlContent = """
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

		try (FileWriter xmlWriter = new FileWriter(xmlFile)) {
			xmlWriter.write(xmlContent);
		}

		// when
		CtFlussoRiversamento result = service.unmarshaller(xmlFile, CtFlussoRiversamento.class, xsdUrl);

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
		java.nio.file.Files.writeString(xmlFile.toPath(), invalidXmlContent);

		// when then
		assertThrows(ActivitiesException.class, () ->
			service.unmarshaller(xmlFile, CtFlussoRiversamento.class, xsdUrl), "Error while parsing file"
		);
	}
}
