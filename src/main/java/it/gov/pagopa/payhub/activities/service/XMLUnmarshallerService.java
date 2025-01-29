package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A reusable service for generic XML unmarshalling.
 */
@Lazy
@Slf4j
@Service
public class XMLUnmarshallerService {

	/**
	 * Unmarshals an XML file into a Java object.
	 *
	 * @param <T>         the type of the resulting Java object
	 * @param file        the XML file to parse
	 * @param clazz       the class type to which the XML should be unmarshalled
	 * @param jaxbContext the pre-configured JAXBContext instance
	 * @param schema      the pre-configured Schema instance for validation (optional)
	 * @return the unmarshalled Java object of type {@code T}
	 */
	public <T> T unmarshal(File file, Class<T> clazz, JAXBContext jaxbContext, Schema schema) {
		try (InputStream is = new FileInputStream(file)) {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			if (schema != null) {
				unmarshaller.setSchema(schema);
			}
			JAXBElement<T> element = unmarshaller.unmarshal(new StreamSource(is), clazz);
			return element.getValue();
		} catch (IOException | JAXBException e) {
			throw new InvalidValueException("Error while parsing file: "+ file.getName());
		}
	}
}
