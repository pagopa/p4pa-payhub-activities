package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.XMLUnmarshallerService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

/**
 * Handles the deserialization of files conforming to the "FlussoRiversamento" schema.
 */
@Lazy
@Component
public class FlussoRiversamentoHandler {
	private final JAXBContext jaxbContext;
	private final Schema schema;
	private final XMLUnmarshallerService xmlUnmarshallerService;

	/**
	 * Initializes the handler with pre-configured JAXBContext and Schema for FlussoRiversamento.
	 *
	 * @param paymetsReportingXsdResource the XSD Resource
	 * @param xmlUnmarshallerService the xml unmarshalling service
	 */
	public FlussoRiversamentoHandler(@Value("classpath:xsd/FlussoRiversamento.xsd") Resource paymetsReportingXsdResource,
	                                 XMLUnmarshallerService xmlUnmarshallerService) {
		try {
			this.jaxbContext = JAXBContext.newInstance(CtFlussoRiversamento.class);
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			this.schema = schemaFactory.newSchema(paymetsReportingXsdResource.getURL());
			this.xmlUnmarshallerService = xmlUnmarshallerService;
		} catch (JAXBException | SAXException | IOException e) {
			throw new ActivitiesException("Error while creating a new instance for CtFlussoRiversamento");
		}
	}

	/**
	 * Unmarshals a file into a CtFlussoRiversamento object.
	 *
	 * @param file the XML file to parse
	 * @return the unmarshalled CtFlussoRiversamento object
	 */
	public CtFlussoRiversamento handle(File file) {
		return xmlUnmarshallerService.unmarshal(file, CtFlussoRiversamento.class, jaxbContext, schema);
	}
}
