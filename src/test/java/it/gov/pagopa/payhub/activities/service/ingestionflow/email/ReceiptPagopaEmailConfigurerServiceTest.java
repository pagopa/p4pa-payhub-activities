package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaEmailConfigurerServiceTest {

  @InjectMocks
  private ReceiptPagopaEmailConfigurerService receiptPagopaEmailConfigurerService;

  @Mock
  private EmailTemplate emailTemplateMock;

  //region retrieveRecipients
  @Test
  void givenValidReceiptAndInstallmentWhenRetrieveRecipientsThenOk(){
    //given
    ReceiptDTO receiptDTO = new ReceiptDTO()
      .debtor(new PersonDTO().email("receiptDebtor@mail.it"))
      .payer(new PersonDTO().email("receiptPayer@mail.it"));
    InstallmentDTO installmentDTO = new InstallmentDTO()
      .debtor(new PersonDTO().email("installmentDebtor@mail.it"));
    //when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptDTO, installmentDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("installmentDebtor@mail.it", result.get(0));
    Assertions.assertEquals("receiptPayer@mail.it", result.get(1));
  }

  @Test
  void givenValidReceiptAndInstallmentWithNoMailWhenRetrieveRecipientsThenOk(){
    //given
    ReceiptDTO receiptDTO = new ReceiptDTO()
      .debtor(new PersonDTO().email("receiptDebtor@mail.it"))
      .payer(new PersonDTO().email("receiptPayer@mail.it"));
    InstallmentDTO installmentDTO = new InstallmentDTO()
      .debtor(new PersonDTO().email(null));
    //when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptDTO, installmentDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("receiptDebtor@mail.it", result.get(0));
    Assertions.assertEquals("receiptPayer@mail.it", result.get(1));
  }

  @Test
  void givenValidReceiptWithNoDebtorMailAndInstallmentWhenRetrieveRecipientsThenOk(){
    //given
    ReceiptDTO receiptDTO = new ReceiptDTO()
      .debtor(new PersonDTO().email(null))
      .payer(new PersonDTO().email("receiptPayer@mail.it"));
    InstallmentDTO installmentDTO = new InstallmentDTO()
      .debtor(new PersonDTO().email("installmentDebtor@mail.it"));
    //when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptDTO, installmentDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals("installmentDebtor@mail.it", result.get(0));
    Assertions.assertEquals("receiptPayer@mail.it", result.get(1));
  }

  @Test
  void givenValidReceiptWithNoMailAndInstallmentWhenRetrieveRecipientsThenOk(){
    //given
    ReceiptDTO receiptDTO = new ReceiptDTO()
      .debtor(new PersonDTO().email(null))
      .payer(new PersonDTO().email(null));
    InstallmentDTO installmentDTO = new InstallmentDTO()
      .debtor(new PersonDTO().email("installmentDebtor@mail.it"));
    //when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptDTO, installmentDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("installmentDebtor@mail.it", result.get(0));
  }

  @Test
  void givenValidReceiptWithNoMailAndInstallmentWithNoMailWhenRetrieveRecipientsThenEmpty(){
    //given
    ReceiptDTO receiptDTO = new ReceiptDTO()
      .debtor(new PersonDTO().email(null))
      .payer(new PersonDTO().email(null));
    InstallmentDTO installmentDTO = new InstallmentDTO()
      .debtor(new PersonDTO().email(null));
    //when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptDTO, installmentDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.size());
  }
  //endregion

  @Test
  void givenValidTemplateWhenConfigureThenOk(){
    //given
    Mockito.when(emailTemplateMock.getSubject()).thenReturn("subject");
    Mockito.when(emailTemplateMock.getBody()).thenReturn("body {noticeNumber}");
    ReceiptDTO receiptDTO = new ReceiptDTO()
      .companyName("companyName")
      .orgFiscalCode("orgFiscalCode")
      .noticeNumber("noticeNumber")
      .paymentAmountCents(123456L)
      .paymentDateTime(OffsetDateTime.of(2025, 2, 21, 10, 30, 23, 0, ZoneOffset.UTC));

    //when
    EmailDTO response = receiptPagopaEmailConfigurerService.configure(receiptDTO);

    //verify
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getParams());
    Assertions.assertNotNull(response.getMailSubject());
    Assertions.assertNotNull(response.getHtmlText());
    Assertions.assertEquals("subject", response.getMailSubject());
    Assertions.assertEquals("body noticeNumber", response.getHtmlText());
    Assertions.assertEquals("companyName", response.getParams().get("companyName"));
    Assertions.assertEquals("orgFiscalCode", response.getParams().get("orgFiscalCode"));
    Assertions.assertEquals("noticeNumber", response.getParams().get("noticeNumber"));
    Assertions.assertEquals("1.234,56 €", response.getParams().get("amount"));
    Assertions.assertEquals("21/02/2025 10:30:23", response.getParams().get("paymentDate"));
  }

}
