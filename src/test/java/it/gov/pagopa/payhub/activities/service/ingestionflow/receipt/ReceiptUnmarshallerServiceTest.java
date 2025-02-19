package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.payhub.activities.service.XMLUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class ReceiptUnmarshallerServiceTest {

  private final ReceiptUnmarshallerService receiptUnmarshallerService;

  ReceiptUnmarshallerServiceTest() {
    this.receiptUnmarshallerService = new ReceiptUnmarshallerService(
      new ClassPathResource("receipt/wsdl/xsd/paForNode.xsd"),
        new XMLUnmarshallerService()
    );
  }

  @Test
  void givenValidReceiptWhenUnmarshalReceiptPagopaThenOk() throws IOException, DatatypeConfigurationException {
    //given
    Resource xmlFile = new ClassPathResource("receipt/RECEIPT_VALID.xml");

    //when
    PaSendRTV2Request response = receiptUnmarshallerService.unmarshalReceiptPagopa(xmlFile.getFile());

    //verify
    Assertions.assertNotNull(response);
    Assertions.assertEquals("77777777777", response.getIdPA());
    Assertions.assertEquals(
      DatatypeFactory.newInstance().newXMLGregorianCalendar(2021, 10, 1, 17, 48, 22, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED),
      response.getReceipt().getPaymentDateTime());
    Assertions.assertEquals("JHNDOE00A01F205N", response.getReceipt().getDebtor().getUniqueIdentifier().getEntityUniqueIdentifierValue());
    Assertions.assertEquals(2, response.getReceipt().getTransferList().getTransfers().size());
    Assertions.assertEquals("0201102IM", response.getReceipt().getTransferList().getTransfers().get(1).getTransferCategory());
    Assertions.assertEquals(1, response.getReceipt().getMetadata().getMapEntries().size());
    Assertions.assertEquals("keytest3", response.getReceipt().getMetadata().getMapEntries().get(0).getKey());
  }

  @Test
  void givenInvalidXmlWhenUnmarshalReceiptPagopaThenException() throws IOException {
    // given
    Resource xmlFile = new ClassPathResource("receipt/RECEIPT_INVALID.xml");

    // when then
    File file = xmlFile.getFile();
    Assertions.assertThrows(InvalidValueException.class, () -> receiptUnmarshallerService.unmarshalReceiptPagopa(file));
  }

  @Test
  void testJAXBExceptionInConstructor() {
    try(MockedStatic<JAXBContext> mockedStatic = Mockito.mockStatic(JAXBContext.class)) {
      mockedStatic.when(() -> JAXBContext.newInstance(PaSendRTV2Request.class))
        .thenThrow(new JAXBException("Simulated JAXBException"));
      Assertions.assertThrows(IllegalStateException.class, () -> new ReceiptUnmarshallerService(null, null));
    }
  }

}
