package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt.ReceiptPagopaSendEmailActivityImpl;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ResolvedInstallmentResult;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.ReceiptPagoPaEmailConfigurerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptInstallmentResolverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaSendEmailActivityTest {


  @Mock
  private ReceiptPagoPaEmailConfigurerService receiptPagoPaEmailConfigurerServiceMock;
  @Mock
  private SendEmailActivity sendEmailActivityMock;
  @Mock
  private ReceiptService receiptServiceMock;
  @Mock
  private ReceiptInstallmentResolverService receiptInstallmentResolverServiceMock;

  @InjectMocks
  private ReceiptPagopaSendEmailActivityImpl receiptPagopaSendEmailActivity;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
            sendEmailActivityMock,
            receiptServiceMock
    );
  }

  @Test
  void givenValidReceiptWhenSendReceiptHandledEmailThenOk() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("orgFiscalCode", null, null);
    InstallmentDTO installmentDTO = new InstallmentDTO();
    Organization organization = new Organization().organizationId(1L).brokerId(1L);
    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(installmentDTO, List.of(), organization);

    List<String> recipients = List.of("recipient1", "recipient2");
    Map<String, String> params = Map.of();
    FileResourceDTO attachment = mock(FileResourceDTO.class);

    when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);
    when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptDTO, installmentDTO)).thenReturn(recipients);
    when(receiptPagoPaEmailConfigurerServiceMock.buildTemplateParams(receiptDTO)).thenReturn(params);
    when(receiptServiceMock.getReceiptPdf(eq(receiptDTO.getReceiptId()), anyLong())).thenReturn(attachment);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    verify(receiptPagoPaEmailConfigurerServiceMock).retrieveRecipients(receiptDTO, installmentDTO);
    verify(receiptPagoPaEmailConfigurerServiceMock).buildTemplateParams(receiptDTO);
    verify(sendEmailActivityMock).sendTemplatedEmail(
            Mockito.eq(1L),
            Mockito.argThat(e ->
                    Arrays.equals(e.getTo(), recipients.toArray(new String[0])) &&
                            e.getParams() == params &&
                              EmailTemplateName.INGESTION_PAGOPA_RT.equals(e.getTemplateName()) //&&
            )
    );
  }

  @Test
  void givenResolvedInstallmentNullWhenSendReceiptHandledEmailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("orgFiscalCode", null, null);
    Organization organization = new Organization().organizationId(1L).brokerId(1L);
    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(null, List.of(), organization);

    when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    Mockito.verifyNoInteractions(receiptPagoPaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

  @Test
  void givenEmptyResolvedResultWhenSendReceiptHandledEmailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("UNKNOWN_orgFiscalCode", null, null);

    when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO))
            .thenReturn(ResolvedInstallmentResult.empty());

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    Mockito.verifyNoInteractions(receiptPagoPaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

  @Test
  void givenNoReceiptIdWhenSendReceiptHandledEmailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = new ReceiptWithAdditionalNodeDataDTO();

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    Mockito.verifyNoInteractions(receiptPagoPaEmailConfigurerServiceMock, sendEmailActivityMock, receiptInstallmentResolverServiceMock);
  }

  @Test
  void givenNoRecipientsWhenSendReceiptHandledEmailThenNotSent() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("orgFiscalCode", null, null);
    InstallmentDTO installmentDTO = new InstallmentDTO();
    Organization organization = new Organization().organizationId(1L).brokerId(1L);
    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(installmentDTO, List.of(), organization);

    when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);
    when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptDTO, installmentDTO)).thenReturn(List.of());

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    verify(receiptPagoPaEmailConfigurerServiceMock).retrieveRecipients(receiptDTO, installmentDTO);
    Mockito.verifyNoMoreInteractions(receiptPagoPaEmailConfigurerServiceMock, sendEmailActivityMock);
  }

  @Test
  void givenNullPaymentDateTimeWhenSendReceiptHandledEmailThenAttachmentWithOriginalFilename() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("orgFiscalCode", null, null);
    String expectedFilename = "original_filename.txt";

    InstallmentDTO installmentDTO = new InstallmentDTO();
    Organization organization = new Organization().organizationId(1L).brokerId(1L);
    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(installmentDTO, List.of(), organization);
    FileResourceDTO attachment = new FileResourceDTO();
    attachment.setFileName(expectedFilename);
    ArgumentCaptor<TemplatedEmailDTO> captor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

    when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);
    when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptDTO, installmentDTO)).thenReturn(List.of("recipient1"));
    when(receiptServiceMock.getReceiptPdf(eq(receiptDTO.getReceiptId()), anyLong())).thenReturn(attachment);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.eq(1L), captor.capture());
    Assertions.assertEquals(expectedFilename, attachment.getFileName());
  }

  @Test
  void givenNullOriginalFilenameWhenSendReceiptHandledEmailThenAttachmentWithNewFilenameAndDefaultExtension() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("orgFiscalCode", OffsetDateTime.now(), "noticeNumber");
    String expectedFilename = Objects.requireNonNull(receiptDTO.getPaymentDateTime()).toLocalDate() + "-" + receiptDTO.getNoticeNumber() + ".pdf";

    InstallmentDTO installmentDTO = new InstallmentDTO();
    Organization organization = new Organization().organizationId(1L).brokerId(1L);
    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(installmentDTO, List.of(), organization);
    FileResourceDTO attachment = new FileResourceDTO();
    ArgumentCaptor<TemplatedEmailDTO> captor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

    when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);
    when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptDTO, installmentDTO)).thenReturn(List.of("recipient1"));
    when(receiptServiceMock.getReceiptPdf(eq(receiptDTO.getReceiptId()), anyLong())).thenReturn(attachment);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.eq(1L), captor.capture());
    Assertions.assertEquals(expectedFilename, attachment.getFileName());
  }

  @Test
  void givenOriginalFilenameWhenSendReceiptHandledEmailThenAttachmentWithNewFilenameAndOriginalExtension() {
    // Given
    ReceiptWithAdditionalNodeDataDTO receiptDTO = buildReceipt("orgFiscalCode", OffsetDateTime.now(), "noticeNumber");
    String expectedFilename = Objects.requireNonNull(receiptDTO.getPaymentDateTime()).toLocalDate() + "-" + receiptDTO.getNoticeNumber() + ".txt";

    InstallmentDTO installmentDTO = new InstallmentDTO();
    Organization organization = new Organization().organizationId(1L).brokerId(1L);
    ResolvedInstallmentResult resolved = new ResolvedInstallmentResult(installmentDTO, List.of(), organization);
    FileResourceDTO attachment = new FileResourceDTO();
    attachment.setFileName("original_filename.txt");
    ArgumentCaptor<TemplatedEmailDTO> captor = ArgumentCaptor.forClass(TemplatedEmailDTO.class);

    when(receiptInstallmentResolverServiceMock.resolveInstallment(receiptDTO)).thenReturn(resolved);
    when(receiptPagoPaEmailConfigurerServiceMock.retrieveRecipients(receiptDTO, installmentDTO)).thenReturn(List.of("recipient1"));
    when(receiptServiceMock.getReceiptPdf(eq(receiptDTO.getReceiptId()), anyLong())).thenReturn(attachment);

    // When
    Assertions.assertDoesNotThrow(() -> receiptPagopaSendEmailActivity.sendReceiptHandledEmail(receiptDTO));

    // Then
    verify(sendEmailActivityMock).sendTemplatedEmail(Mockito.eq(1L), captor.capture());
    Assertions.assertEquals(expectedFilename, attachment.getFileName());
  }

  private static ReceiptWithAdditionalNodeDataDTO buildReceipt(String orgFiscalCode,
                                                               OffsetDateTime paymentDateTime, String noticeNumber) {
    ReceiptWithAdditionalNodeDataDTO dto = new ReceiptWithAdditionalNodeDataDTO();
    dto.setReceiptId(1L);
    dto.setOrgFiscalCode(orgFiscalCode);
    dto.setPaymentDateTime(paymentDateTime);
    dto.setNoticeNumber(noticeNumber);
    return dto;
  }
}