package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.XMLUnmarshallerService;
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

import java.io.IOException;


@ExtendWith(MockitoExtension.class)
class TreasuryUnmarshallerServiceTest {

	private Resource xsdOpi14Resource;
	private Resource xsdOpi161Resource;
	private TreasuryUnmarshallerService treasuryUnmarshallerService;

	@BeforeEach
	void setUp() {
		XMLUnmarshallerService xmlUnmarshallerService = new XMLUnmarshallerService();
		xsdOpi14Resource = new ClassPathResource("xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xsd");
		xsdOpi161Resource = new ClassPathResource("xsd/OPI_GIORNALE_DI_CASSA_V_1_6_1.xsd");
		treasuryUnmarshallerService = new TreasuryUnmarshallerService(xsdOpi14Resource, xsdOpi161Resource, xmlUnmarshallerService);
	}

	@Test
	void givenValidXmlWhenUnmarshalOpi14ThenOk() throws Exception {
		// given
		Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_4.VALID.xml");

		//when
		it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa result = treasuryUnmarshallerService.unmarshalOpi14(xmlFile.getFile());

		// then
		Assertions.assertNotNull(result);
		Assertions.assertEquals("2024-04-07ABI03062-315V900103811327", result.getId());
		// TODO other equals
	}

	@Test
	void givenValidXmlWhenUnmarshalOpi161ThenOk() throws Exception {
		// given
		Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_6_1.VALID.xml");

		//when
		it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa result = treasuryUnmarshallerService.unmarshalOpi161(xmlFile.getFile());

		// then
		Assertions.assertNotNull(result);
		Assertions.assertEquals("2024-04-07ABI03062-315V900103811327", result.getId());
		// TODO other equals
	}

	@Test
	void givenInvalidXmlWhenUnmarshalOpi14ThenException() {
		// given
		Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_4.INVALID.xml");

		// when then
		Assertions.assertThrows(ActivitiesException.class,
			() -> treasuryUnmarshallerService.unmarshalOpi14(xmlFile.getFile()), "Error while parsing file"
		);
	}

	@Test
	void givenInvalidXmlWhenUnmarshalOpi161ThenException() {
		// given
		Resource xmlFile = new ClassPathResource("treasury/OPI_GIORNALE_DI_CASSA_V_1_6_1.INVALID.xml");

		// when then
		Assertions.assertThrows(ActivitiesException.class,
			() -> treasuryUnmarshallerService.unmarshalOpi161(xmlFile.getFile()), "Error while parsing file"
		);
	}


	@Test
	void testJAXBExceptionInConstructorOpi14() {
		try(MockedStatic<JAXBContext> mockedStatic = Mockito.mockStatic(JAXBContext.class)) {
			mockedStatic.when(() -> JAXBContext.newInstance(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa.class))
				.thenThrow(new JAXBException("Simulated JAXBException"));
			Assertions.assertThrows(ActivitiesException.class, () -> new TreasuryUnmarshallerService(xsdOpi14Resource, xsdOpi161Resource, null));
		}
	}

	@Test
	void testJAXBExceptionInConstructorOpi161() {
		try(MockedStatic<JAXBContext> mockedStatic = Mockito.mockStatic(JAXBContext.class)) {
			mockedStatic.when(() -> JAXBContext.newInstance(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class))
				.thenThrow(new JAXBException("Simulated JAXBException"));
			Assertions.assertThrows(ActivitiesException.class, () -> new TreasuryUnmarshallerService(xsdOpi14Resource, xsdOpi161Resource, null));
		}
	}

	@Test
	void testIOExceptionInConstructor() throws Exception {
		// given
		Resource mockResource = Mockito.mock(Resource.class);
		Mockito.when(mockResource.getURL()).thenThrow(new IOException("Simulated IOException"));

		// when then
		Assertions.assertThrows(ActivitiesException.class, () -> new TreasuryUnmarshallerService(mockResource, xsdOpi161Resource, null));
	}
}
