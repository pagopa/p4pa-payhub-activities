package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.service.XMLUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
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

@Lazy
@Component
@Slf4j
public class ReceiptUnmarshallerService {

  private final JAXBContext jaxbContextPagopa;
  private final Schema schemaPagopa;
  private final XMLUnmarshallerService xmlUnmarshallerService;

  /**
   * Initializes the handler with pre-configured JAXBContext and Schema for ReceiptPagopa.
   *
   * @param xsdSchemaResourceReceiptPagopa the XSD Resource of Receipt Pagopa
   * @param xmlUnmarshallerService the xml unmarshalling service
   */
  public ReceiptUnmarshallerService(@Value("classpath:receipt/wsdl/xsd/paForNode.xsd") Resource xsdSchemaResourceReceiptPagopa,
                                    XMLUnmarshallerService xmlUnmarshallerService) {
    try {
      this.jaxbContextPagopa = JAXBContext.newInstance(PaSendRTV2Request.class);
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      this.schemaPagopa = schemaFactory.newSchema(xsdSchemaResourceReceiptPagopa.getURL());
      this.xmlUnmarshallerService = xmlUnmarshallerService;
    } catch (JAXBException | SAXException | IOException e) {
      log.error("Error while creating a new instance for ReceiptUnmarshallerService", e);
      throw new IllegalStateException("Error while creating a new instance for ReceiptUnmarshallerService");
    }
  }

  /**
   * Unmarshals a Receipt Pagopa file into a PaSendRTV2Request object.
   *
   * @param file the XML file to parse
   * @return the unmarshalled FlussoGiornaleDiCassa object
   */
  public PaSendRTV2Request unmarshalReceiptPagopa(File file) {
    return xmlUnmarshallerService.unmarshal(file, PaSendRTV2Request.class, jaxbContextPagopa, schemaPagopa);
  }
}
