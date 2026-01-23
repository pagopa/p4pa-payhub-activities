package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.payhub.activities.service.files.XMLUnmarshallerService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class TreasuryUnmarshallerServiceTest {

    private TreasuryUnmarshallerService treasuryUnmarshallerService;


    @BeforeEach
    void setUp() {
        XMLUnmarshallerService xmlUnmarshallerService = new XMLUnmarshallerService();
        Resource resourceV1_4 = new ClassPathResource("xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xsd");
        Resource resourceV1_6_1 = new ClassPathResource("xsd/OPI_GIORNALE_DI_CASSA_V_1_6_1.xsd");
        Resource resourceV1_7_1 = new ClassPathResource("xsd/OPI_GIORNALE_DI_CASSA_V_1_7_1.xsd");
        treasuryUnmarshallerService = new TreasuryUnmarshallerService(resourceV1_4, resourceV1_6_1, resourceV1_7_1, xmlUnmarshallerService);
    }

    @Test
    void givenValidXmlWhenUnmarshalOpi14ThenOk() throws Exception {
        // given
        Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_4.VALID.xml");

        //when
        it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa result = treasuryUnmarshallerService.unmarshalOpi14(xmlFile.getFile());

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("GDC-202209302022202209291010285#001#001", result.getIdentificativoFlussoBT().getFirst());

    }

    @Test
    void givenValidXmlWhenUnmarshalOpi161ThenOk() throws Exception {
        // given
        Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_6_1.VALID.xml");
        File file = xmlFile.getFile();

        //when
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa result = treasuryUnmarshallerService.unmarshalOpi161(file);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("GDC-202209302022202209291010285#001#001", result.getIdentificativoFlussoBT().getFirst());
    }

    @Test
    void givenInvalidXmlWhenUnmarshalOpi14ThenException() throws IOException {
        // given
        Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_4.INVALID.xml");

        // when then
        File file = xmlFile.getFile();
        Assertions.assertThrows(InvalidValueException.class,
                () -> treasuryUnmarshallerService.unmarshalOpi14(file),
                "Error while parsing file"
        );
    }

    @Test
    void givenInvalidXmlWhenUnmarshalOpi161ThenException() throws IOException {
        // given
        Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_6_1.INVALID.xml");

        // when then
        File file = xmlFile.getFile();
        Assertions.assertThrows(InvalidValueException.class,
                () -> treasuryUnmarshallerService.unmarshalOpi161(file),
                "Error while parsing file"
        );
    }

    @Test
    void givenValidXmlWhenUnmarshalOpi171ThenOk() throws Exception {
        // given
        Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_7_1.VALID.xml");
        File file = xmlFile.getFile();

        //when
        it.gov.pagopa.payhub.activities.xsd.treasury.opi171.FlussoGiornaleDiCassa result = treasuryUnmarshallerService.unmarshalOpi171(file);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("GDC-202209302022202209291010285#001#001", result.getIdentificativoFlussoBT().getFirst());
    }

    @Test
    void givenInvalidXmlWhenUnmarshalOpi171ThenException() throws IOException {
        // given
        Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_7_1.INVALID.xml");

        // when then
        File file = xmlFile.getFile();
        Assertions.assertThrows(InvalidValueException.class,
                () -> treasuryUnmarshallerService.unmarshalOpi171(file),
                "Error while parsing file"
        );
    }

    @Test
    void testJAXBExceptionInConstructorOpi14() {
        try (MockedStatic<JAXBContext> mockedStatic = Mockito.mockStatic(JAXBContext.class)) {
            mockedStatic.when(() -> JAXBContext.newInstance(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa.class))
                    .thenThrow(new JAXBException("Simulated JAXBException"));
            Assertions.assertThrows(IllegalStateException.class, () -> new TreasuryUnmarshallerService(null, null, null, null));
        }
    }

    @Test
    void testJAXBExceptionInConstructorOpi161() {
        try (MockedStatic<JAXBContext> mockedStatic = Mockito.mockStatic(JAXBContext.class)) {
            mockedStatic.when(() -> JAXBContext.newInstance(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class))
                    .thenThrow(new JAXBException("Simulated JAXBException"));
            Assertions.assertThrows(IllegalStateException.class, () -> new TreasuryUnmarshallerService(null, null, null, null));
        }
    }

    @Test
    void testJAXBExceptionInConstructorOpi171() {
        try (MockedStatic<JAXBContext> mockedStatic = Mockito.mockStatic(JAXBContext.class)) {
            mockedStatic.when(() -> JAXBContext.newInstance(it.gov.pagopa.payhub.activities.xsd.treasury.opi171.FlussoGiornaleDiCassa.class))
                    .thenThrow(new JAXBException("Simulated JAXBException"));
            Assertions.assertThrows(IllegalStateException.class, () -> new TreasuryUnmarshallerService(null, null, null, null));
        }
    }

    @Test
    void testIOExceptionInConstructor() throws Exception {
        // given
        Resource mockResource = Mockito.mock(Resource.class);
        Mockito.when(mockResource.getURL()).thenThrow(new IOException("Simulated IOException"));

        // when then
        Assertions.assertThrows(IllegalStateException.class, () -> new TreasuryUnmarshallerService(mockResource, null, null, null));
    }
}
