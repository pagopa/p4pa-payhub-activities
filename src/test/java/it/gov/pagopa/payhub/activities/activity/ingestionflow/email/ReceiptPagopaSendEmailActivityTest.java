package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt.ReceiptPagopaSendEmailActivityImpl;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagoPaEmailConfigurerService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaSendEmailActivityTest {

  public static final String MAIL_SENDER_ADDRESS = "sender@email.com";

  @Mock
  private ReceiptPagoPaEmailConfigurerService receiptPagoPaEmailConfigurerServiceMock;

  @Mock
  private SendEmailActivity sendEmailActivityMock;
  @Mock
  private ReceiptService receiptServiceMock;
  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private BrokerService brokerServiceMock;

  @InjectMocks
  private ReceiptPagopaSendEmailActivityImpl receiptPagopaSendEmailActivity;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
            sendEmailActivityMock,
            receiptServiceMock,
            organizationServiceMock,
            brokerServiceMock
    );
  }

  @Test
  void givenValidReceiptAndInstallmentWhenSendReceiptHandledEmailMailThenOk() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptWithAdditionalNodeDataDTO.setReceiptId(1L);
    receiptWithAdditionalNodeDataDTO.setOrgFiscalCode("orgFiscalCode");
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1", "recipient2");
    Map<String, String> params = Map.of();
    FileResourceDTO attachment = mock(FileResourceDTO.class);
    Broker broker = mock(Broker.class);
    Mockito.when(broker.getBrokerId()).thenReturn(1L);
    BrokerConfiguration brokerConfiguration = mock(BrokerConfiguration.class);
    Mockito.when(brokerConfiguration.getMailSenderAddress()).thenReturn(MAIL_SENDER_ADDRESS);

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.buildTemplateParams(receiptWithAdditionalNodeDataDTO)).thenReturn(params);
    Mockito.when(receiptServiceMock.getReceiptPdf(eq(receiptWithAdditionalNodeDataDTO.getReceiptId()), anyLong())).thenReturn(attachment);
    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode(receiptWithAdditionalNodeDataDTO.getOrgFiscalCode())).thenReturn(Optional.of(new Organization().organizationId(1L).brokerId(1L)));
    Mockito.when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
    Mockito.when(brokerServiceMock.getBrokerConfigurationsById(1L)).thenReturn(brokerConfiguration);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock, Mockito.times(1)).buildTemplateParams(receiptWithAdditionalNodeDataDTO);
    Mockito.verify(sendEmailActivityMock, Mockito.times(1)).sendTemplatedEmail(
      Mockito.eq(1L),
      Mockito.argThat(
              e ->
                      Arrays.equals(
                              e.getTo(), recipients.toArray(new String[0])) &&
                              e.getParams() == params &&
                              EmailTemplateName.INGESTION_PAGOPA_RT.equals(e.getTemplateName()) &&
                              e.getFrom().equals(MAIL_SENDER_ADDRESS)
      )
    );
  }

  @Test
  void givenNoInstallmentWhenSendReceiptHandledEmailMailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptWithAdditionalNodeDataDTO.setOrgFiscalCode("11111111111");

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
    receiptWithAdditionalNodeDataDTO.setOrgFiscalCode("11111111111");
    List<String> recipients = List.of();

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock, Mockito.times(1)).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verifyNoMoreInteractions(receiptPagoPaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

    @Test
    void givenUnknownOrgFiscalCodeWhenSendReceiptHandledEmailThenNotSent() {
        ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();
        receiptDTO.setOrgFiscalCode("UNKNOWN_11111111111");
        InstallmentDTO installmentDTO = new InstallmentDTO();

        receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO, installmentDTO);

        Mockito.verifyNoInteractions(receiptPagoPaEmailConfigurerServiceMock, organizationServiceMock, sendEmailActivityMock);
    }

  @Test
  void givenReceiptWithNullPaymentDateTimeWhenSendReceiptHandledEmailMailThenAttachmentWithOriginalFilename() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = getReceiptWithAdditionalNodeData(null, null);
    String expectedFilename = "original_filename.txt";

    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1");
    FileResourceDTO attachment = new FileResourceDTO();
    attachment.setFileName(expectedFilename);
    Broker broker = mock(Broker.class);
    Mockito.when(broker.getBrokerId()).thenReturn(1L);
    BrokerConfiguration brokerConfiguration = mock(BrokerConfiguration.class);
    Mockito.when(brokerConfiguration.getMailSenderAddress()).thenReturn(MAIL_SENDER_ADDRESS);

    ArgumentCaptor<TemplatedEmailDTO> templatedEmailDTOArgumentCaptor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode(receiptWithAdditionalNodeDataDTO.getOrgFiscalCode())).thenReturn(Optional.of(new Organization().organizationId(1L).brokerId(1L)));
    Mockito.when(receiptServiceMock.getReceiptPdf(eq(receiptWithAdditionalNodeDataDTO.getReceiptId()), anyLong())).thenReturn(attachment);
    Mockito.when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
    Mockito.when(brokerServiceMock.getBrokerConfigurationsById(1L)).thenReturn(brokerConfiguration);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).buildTemplateParams(receiptWithAdditionalNodeDataDTO);
    Mockito.verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.eq(1L), templatedEmailDTOArgumentCaptor.capture());

    TemplatedEmailDTO templatedEmailDTO = templatedEmailDTOArgumentCaptor.getValue();

    Assertions.assertArrayEquals(templatedEmailDTO.getTo(), recipients.toArray(new String[0]));
    Assertions.assertEquals(EmailTemplateName.INGESTION_PAGOPA_RT, templatedEmailDTO.getTemplateName());
    Assertions.assertEquals(expectedFilename, attachment.getFileName());
    Assertions.assertEquals(MAIL_SENDER_ADDRESS, templatedEmailDTO.getFrom());
  }

  private static ReceiptWithAdditionalNodeDataDTO getReceiptWithAdditionalNodeData(OffsetDateTime paymentDateTime, String noticeNumber) {
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
    receiptWithAdditionalNodeDataDTO.setReceiptId(1L);
    receiptWithAdditionalNodeDataDTO.setOrgFiscalCode("orgFiscalCode");
    receiptWithAdditionalNodeDataDTO.setPaymentDateTime(paymentDateTime);
    receiptWithAdditionalNodeDataDTO.setNoticeNumber(noticeNumber);
    return receiptWithAdditionalNodeDataDTO;
  }

  @Test
  void givenReceiptWithNullOriginalFilenameWhenSendReceiptHandledEmailMailThenAttachmentWithNewFilenameAndDefaultExtension() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = getReceiptWithAdditionalNodeData(OffsetDateTime.now(), "noticeNumber");
    String expectedFilename = receiptWithAdditionalNodeDataDTO.getPaymentDateTime().toLocalDate() + "-" + receiptWithAdditionalNodeDataDTO.getNoticeNumber() + ".pdf";

    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1");
    FileResourceDTO attachment = new FileResourceDTO();
    Broker broker = mock(Broker.class);
    Mockito.when(broker.getBrokerId()).thenReturn(1L);
    BrokerConfiguration brokerConfiguration = mock(BrokerConfiguration.class);
    Mockito.when(brokerConfiguration.getMailSenderAddress()).thenReturn(MAIL_SENDER_ADDRESS);

    ArgumentCaptor<TemplatedEmailDTO> templatedEmailDTOArgumentCaptor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode(receiptWithAdditionalNodeDataDTO.getOrgFiscalCode())).thenReturn(Optional.of(new Organization().organizationId(1L).brokerId(1L)));
    Mockito.when(receiptServiceMock.getReceiptPdf(eq(receiptWithAdditionalNodeDataDTO.getReceiptId()), anyLong())).thenReturn(attachment);
    Mockito.when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
    Mockito.when(brokerServiceMock.getBrokerConfigurationsById(1L)).thenReturn(brokerConfiguration);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).buildTemplateParams(receiptWithAdditionalNodeDataDTO);
    Mockito.verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.eq(1L), templatedEmailDTOArgumentCaptor.capture());

    TemplatedEmailDTO templatedEmailDTO = templatedEmailDTOArgumentCaptor.getValue();

    Assertions.assertArrayEquals(templatedEmailDTO.getTo(), recipients.toArray(new String[0]));
    Assertions.assertEquals(EmailTemplateName.INGESTION_PAGOPA_RT, templatedEmailDTO.getTemplateName());
    Assertions.assertEquals(expectedFilename, attachment.getFileName());
    Assertions.assertEquals(MAIL_SENDER_ADDRESS, templatedEmailDTO.getFrom());
  }

  @Test
  void givenReceiptWithOriginalFilenameWhenSendReceiptHandledEmailMailThenAttachmentWithNewFilenameAndOriginalExtension() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = getReceiptWithAdditionalNodeData(OffsetDateTime.now(), "noticeNumber");
    String expectedFilename = receiptWithAdditionalNodeDataDTO.getPaymentDateTime().toLocalDate() + "-" + receiptWithAdditionalNodeDataDTO.getNoticeNumber() + ".txt";

    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1");
    FileResourceDTO attachment = new FileResourceDTO();
    attachment.setFileName("original_filename.txt");
    Broker broker = mock(Broker.class);
    Mockito.when(broker.getBrokerId()).thenReturn(1L);
    BrokerConfiguration brokerConfiguration = mock(BrokerConfiguration.class);
    Mockito.when(brokerConfiguration.getMailSenderAddress()).thenReturn(MAIL_SENDER_ADDRESS);

    ArgumentCaptor<TemplatedEmailDTO> templatedEmailDTOArgumentCaptor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode(receiptWithAdditionalNodeDataDTO.getOrgFiscalCode())).thenReturn(Optional.of(new Organization().organizationId(1L).brokerId(1L)));
    Mockito.when(receiptServiceMock.getReceiptPdf(eq(receiptWithAdditionalNodeDataDTO.getReceiptId()), anyLong())).thenReturn(attachment);
    Mockito.when(brokerServiceMock.getBrokerById(1L)).thenReturn(broker);
    Mockito.when(brokerServiceMock.getBrokerConfigurationsById(1L)).thenReturn(brokerConfiguration);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).buildTemplateParams(receiptWithAdditionalNodeDataDTO);
    Mockito.verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.eq(1L), templatedEmailDTOArgumentCaptor.capture());

    TemplatedEmailDTO templatedEmailDTO = templatedEmailDTOArgumentCaptor.getValue();

    Assertions.assertArrayEquals(templatedEmailDTO.getTo(), recipients.toArray(new String[0]));
    Assertions.assertEquals(EmailTemplateName.INGESTION_PAGOPA_RT, templatedEmailDTO.getTemplateName());
    Assertions.assertEquals(expectedFilename, attachment.getFileName());
    Assertions.assertEquals(MAIL_SENDER_ADDRESS, templatedEmailDTO.getFrom());
  }

  @Test
  void givenEmptyBrokerConfigurationWhenSendReceiptHandledEmailMailThenSendWithNullMailSenderAddress() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = getReceiptWithAdditionalNodeData(OffsetDateTime.now(), "noticeNumber");
    InstallmentDTO installmentDTO = new InstallmentDTO();
    List<String> recipients = List.of("recipient1");
    FileResourceDTO attachment = mock(FileResourceDTO.class);

    ArgumentCaptor<TemplatedEmailDTO> templatedEmailDTOArgumentCaptor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

    Mockito.when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO)).thenReturn(recipients);
    Mockito.when(organizationServiceMock.getOrganizationByFiscalCode(receiptWithAdditionalNodeDataDTO.getOrgFiscalCode())).thenReturn(Optional.of(new Organization().organizationId(1L).brokerId(1L)));
    Mockito.when(receiptServiceMock.getReceiptPdf(eq(receiptWithAdditionalNodeDataDTO.getReceiptId()), anyLong())).thenReturn(attachment);
    Mockito.when(brokerServiceMock.getBrokerById(1L)).thenReturn(null);
    Mockito.when(brokerServiceMock.getBrokerConfigurationsById(1L)).thenReturn(null);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptWithAdditionalNodeDataDTO, installmentDTO));

    // Then
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    Mockito.verify(receiptPagoPaEmailConfigurerServiceMock).buildTemplateParams(receiptWithAdditionalNodeDataDTO);
    Mockito.verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.isNull(), templatedEmailDTOArgumentCaptor.capture());

    TemplatedEmailDTO templatedEmailDTO = templatedEmailDTOArgumentCaptor.getValue();

    Assertions.assertArrayEquals(templatedEmailDTO.getTo(), recipients.toArray(new String[0]));
    Assertions.assertEquals(EmailTemplateName.INGESTION_PAGOPA_RT, templatedEmailDTO.getTemplateName());
    Assertions.assertNull(templatedEmailDTO.getFrom());
  }


}