package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.Objects;

/**
 * A reusable service for generic XML marshalling.
 */
@Lazy
@Slf4j
@Service
public class XMLMarshallerService {

	/**
	 * Serialize a Java class into an XML string
	 */
	public <T> String marshal(Class<T> clazz, T value, JAXBContext jaxbContext) {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			StringWriter stringWriter = new StringWriter();
			String xmlTagName = Objects.requireNonNull(clazz.getAnnotation(XmlType.class), "The provided class has not @XmlType annotation to retrieve tag name").name();
			marshaller.marshal(new JAXBElement<>(QName.valueOf(xmlTagName), clazz, value), stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			String errorMessage = "Error while marshalling object of class " + value.getClass() + ": " + ExceptionUtils.getRootCauseMessage(e);
			log.error(errorMessage, e);
			throw new InvalidValueException(errorMessage);
		}
	}
}
