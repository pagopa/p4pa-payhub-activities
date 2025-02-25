package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt.ReceiptPagopaSendEmailActivityImpl;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagopaEmailConfigurerService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
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
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1", "recipient2");
    EmailDTO emailDTO = new EmailDTO();

    Mockito.when(receiptPagopaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(receiptPagopaEmailConfigurerServiceMock.configure(receiptWithAdditionalNodeDataDTO)).thenReturn(emailDTO);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagopaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verify(receiptPagopaEmailConfigurerServiceMock, Mockito.times(1)).configure(receiptWithAdditionalNodeDataDTO);
    Mockito.verify(sendEmailActivityMock, Mockito.times(1)).send(
      Mockito.argThat(e -> Arrays.equals(e.getTo(), recipients.toArray(new String[0]))));
  }

  @Test
  void givenNoInstallmentWhenSendMailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendEmail(receiptWithAdditionalNodeDataDTO, null));

    // Then
    Mockito.verifyNoInteractions(receiptPagopaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

  @Test
  void givenNoRecipientsWhenSendMailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of();

    Mockito.when(receiptPagopaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagopaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verifyNoMoreInteractions(receiptPagopaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

}