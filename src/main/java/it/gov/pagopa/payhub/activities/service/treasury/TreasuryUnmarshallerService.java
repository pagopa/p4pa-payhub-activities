package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.XMLUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.FlussoGiornaleDiCassa;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

public class TreasuryUnmarshallerService {

    private final JAXBContext jaxbContext;
    private final Schema schema;
    private final XMLUnmarshallerService xmlUnmarshallerService;

    /**
     * Initializes the handler with pre-configured JAXBContext and Schema for FlussoRiversamento.
     *
     * @param xsdSchemaResource the XSD Resource
     * @param xmlUnmarshallerService the xml unmarshalling service
     */
    public TreasuryUnmarshallerService(@Value("classpath:xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xsd") Resource xsdSchemaResource,
                                       XMLUnmarshallerService xmlUnmarshallerService) {
        try {
            this.jaxbContext = JAXBContext.newInstance(FlussoGiornaleDiCassa.class);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.schema = schemaFactory.newSchema(xsdSchemaResource.getURL());
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
    public FlussoGiornaleDiCassa unmarshal(File file) {
        return xmlUnmarshallerService.unmarshal(file, FlussoGiornaleDiCassa.class, jaxbContext, schema);
    }

}
