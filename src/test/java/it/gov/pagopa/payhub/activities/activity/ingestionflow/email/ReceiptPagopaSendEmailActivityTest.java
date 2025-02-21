package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt.ReceiptPagopaSendEmailActivityImpl;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagopaEmailConfigurerService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaSendEmailActivityTest {

  @Mock
  private ReceiptPagopaEmailConfigurerService receiptPagopaEmailConfigurerServiceMock;

  @Mock
  private SendEmailActivity sendEmailActivityMock;

  @InjectMocks
  private ReceiptPagopaSendEmailActivityImpl receiptPagopaSendEmailActivity;


  @Test
  void givenValidReceiptAndInstallmentWhenSendMailThenOk() {
    // Given
    ReceiptDTO receiptDTO = new ReceiptDTO();
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1", "recipient2");
    EmailDTO emailDTO = new EmailDTO();

    Mockito.when(receiptPagopaEmailConfigurerServiceMock.retrieveRecipients(receiptDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(receiptPagopaEmailConfigurerServiceMock.configure(receiptDTO)).thenReturn(emailDTO);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendEmail(receiptDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagopaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptDTO, installmentDTO);
    Mockito.verify(receiptPagopaEmailConfigurerServiceMock, Mockito.times(1)).configure(receiptDTO);
    Mockito.verify(sendEmailActivityMock, Mockito.times(1)).send(
      Mockito.argThat(e -> Arrays.equals(e.getTo(), recipients.toArray(new String[0]))));
  }

  @Test
  void givenNoInstallmentWhenSendMailThenNotSent() {
    // Given
    ReceiptDTO receiptDTO = new ReceiptDTO();

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendEmail(receiptDTO, null));

    // Then
    Mockito.verifyNoInteractions(receiptPagopaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

  @Test
  void givenNoRecipientsWhenSendMailThenNotSent() {
    // Given
    ReceiptDTO receiptDTO = new ReceiptDTO();
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of();

    Mockito.when(receiptPagopaEmailConfigurerServiceMock.retrieveRecipients(receiptDTO, installmentDTO)).thenReturn(recipients);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendEmail(receiptDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagopaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptDTO, installmentDTO);
    Mockito.verifyNoMoreInteractions(receiptPagopaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

}