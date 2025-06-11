package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt.ReceiptPagopaSendEmailActivityImpl;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagoPaEmailConfigurerService;
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
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaSendEmailActivityTest {

  @Mock
  private ReceiptPagoPaEmailConfigurerService receiptPagoPaEmailConfigurerServiceMock;

  @Mock
  private SendEmailActivity sendEmailActivityMock;

  @InjectMocks
  private ReceiptPagopaSendEmailActivityImpl receiptPagopaSendEmailActivity;


  @Test
  void givenValidReceiptAndInstallmentWhenSendReceiptHandledEmailMailThenOk() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1", "recipient2");
    Map<String, String> params = Map.of();

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.buildTemplateParams(receiptWithAdditionalNodeDataDTO)).thenReturn(params);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock, Mockito.times(1)).buildTemplateParams(receiptWithAdditionalNodeDataDTO);
    Mockito.verify(sendEmailActivityMock, Mockito.times(1)).sendTemplatedEmail(
      Mockito.argThat(e -> Arrays.equals(e.getTo(), recipients.toArray(new String[0])) &&
              e.getParams() == params && EmailTemplateName.INGESTION_PAGOPA_RT.equals(e.getTemplateName())));
  }

  @Test
  void givenNoInstallmentWhenSendReceiptHandledEmailMailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, null));

    // Then
    Mockito.verifyNoInteractions(receiptPagoPaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

  @Test
  void givenNoRecipientsWhenSendReceiptHandledEmailMailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of();

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verifyNoMoreInteractions(receiptPagoPaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

}