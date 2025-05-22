package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtReceiptV2;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtSubject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class XMLMarshallerServiceTest {

    private final XMLMarshallerService service = new XMLMarshallerService();

    @Test
    void test() throws JAXBException {
        // Given
        CtReceiptV2 rt = new CtReceiptV2();
        rt.setCompanyName("COMPANY");
        rt.setFiscalCode("FC");
        rt.setPaymentAmount(BigDecimal.TEN);
        CtSubject subject = new CtSubject();
        subject.setFullName("FULLNAME");
        rt.setDebtor(subject);

        // When
        String result = service.marshal(CtReceiptV2.class, rt, JAXBContext.newInstance(CtReceiptV2.class));

        // Then
        Assertions.assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ctReceiptV2><fiscalCode>FC</fiscalCode><paymentAmount>10</paymentAmount><companyName>COMPANY</companyName><debtor><fullName>FULLNAME</fullName></debtor></ctReceiptV2>",
                result
        );
    }
}
