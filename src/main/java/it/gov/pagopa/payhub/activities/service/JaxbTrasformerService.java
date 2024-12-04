package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A service for transforming XML files into Java objects using JAXB.
 * <p>
 * This service supports optional validation of the XML against an XSD schema.
 * It is lazily loaded to optimize startup time and uses SLF4J for logging.
 * </p>
 */
@Lazy
@Slf4j
@Service
public class JaxbTrasformerService {

	/**
	 * Unmarshals an XML file into a Java object of the specified type.
	 *
	 * @param <T>   the type of the resulting Java object
	 * @param file  the XML file to parse
	 * @param clazz the class type to which the XML should be unmarshalled
	 * @param xsd   the URL of the XSD schema to validate the XML against; if {@code null}, no validation is performed
	 * @return the unmarshalled Java object of type {@code T}
	 * @throws ActivitiesException if an error occurs during parsing or validation
	 */
	public <T> T unmarshaller(File file, Class<T> clazz, URL xsd) {
		try (InputStream is = new FileInputStream(file)) {
			Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
			if(xsd != null) {
				SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(xsd);
				unmarshaller.setSchema(schema);
			}
			Source source = new StreamSource(is);
			JAXBElement<T> element = unmarshaller.unmarshal(source, clazz);
			return element.getValue();
		} catch (SAXException | IOException | JAXBException e) {
			throw new ActivitiesException("Error while parsing file: "+ file.getName());
		}
	}
}
