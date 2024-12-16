package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.XMLUnmarshallerService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

@Lazy
@Service
@Slf4j
public class TreasuryUnmarshallerService {

  private final JAXBContext jaxbContextOpi14;
  private final Schema schemaOpi14;
  private final JAXBContext jaxbContextOpi161;
  private final Schema schemaOpi161;
  private final XMLUnmarshallerService xmlUnmarshallerService;

  /**
   * Initializes the handler with pre-configured JAXBContext and Schema for FlussoGiornaleDiCassa.
   *
   * @param xsdSchemaResourceOpi14 the XSD Resource of Opi v1.4
   * @param xsdSchemaResourceOpi161 the XSD Resource of Opi v1.6.1
   * @param xmlUnmarshallerService the xml unmarshalling service
   */
  public TreasuryUnmarshallerService(@Value("classpath:xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xsd") Resource xsdSchemaResourceOpi14,
                                     @Value("classpath:xsd/OPI_GIORNALE_DI_CASSA_V_1_6_1.xsd") Resource xsdSchemaResourceOpi161,
                                     XMLUnmarshallerService xmlUnmarshallerService) {
    try {
      this.jaxbContextOpi14 = JAXBContext.newInstance(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa.class);
      this.jaxbContextOpi161 = JAXBContext.newInstance(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      this.schemaOpi14 = schemaFactory.newSchema(xsdSchemaResourceOpi14.getURL());
      this.schemaOpi161 = schemaFactory.newSchema(xsdSchemaResourceOpi161.getURL());
      this.xmlUnmarshallerService = xmlUnmarshallerService;
    } catch (JAXBException | SAXException | IOException e) {
      log.error("Error while creating a new instance for TreasuryUnmarshallerService", e);
      throw new ActivitiesException("Error while creating a new instance for TreasuryUnmarshallerService");
    }
  }

  /**
   * Unmarshals a OPI v1.4 file into a FlussoGiornaleDiCassa object.
   *
   * @param file the XML file to parse
   * @return the unmarshalled FlussoGiornaleDiCassa object
   */
  public it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa unmarshalOpi14(File file) {
    return xmlUnmarshallerService.unmarshal(file, it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa.class, jaxbContextOpi14, schemaOpi14);
  }

  /**
   * Unmarshals a OPI v1.6.1 file into a FlussoGiornaleDiCassa object.
   *
   * @param file the XML file to parse
   * @return the unmarshalled FlussoGiornaleDiCassa object
   */
  public it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa unmarshalOpi161(File file) {
    return xmlUnmarshallerService.unmarshal(file, it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class, jaxbContextOpi161, schemaOpi161);
  }

}
