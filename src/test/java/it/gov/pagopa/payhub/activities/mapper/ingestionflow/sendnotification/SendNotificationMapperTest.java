package it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class SendNotificationMapperTest {

  private SendNotificationMapper mapper;

  @BeforeEach
  void setup() {
    mapper = new SendNotificationMapper();
  }

  @Test
  void givenFullDtoWhenBuildRequestThenAllFieldsMapped() {
    SendNotificationIngestionFlowFileDTO dto = buildFullDto(true, true, true);

    CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

    Assertions.assertNotNull(result);
    Assertions.assertEquals("prot-123", result.getPaProtocolNumber());
    Assertions.assertEquals("Test Denom", result.getSenderDenomination());
    Assertions.assertEquals(1, result.getRecipients().size());
    Assertions.assertNotNull(result.getRecipients().getFirst().getDigitalDomicile());
    Assertions.assertFalse(result.getRecipients().getFirst().getPayments().isEmpty());
    Assertions.assertFalse(result.getDocuments().isEmpty());
  }

  @Test
  void givenNoDigitalDomicileWhenBuildRequestThenRecipientHasNoDigitalAddress() {
    SendNotificationIngestionFlowFileDTO dto = buildFullDto(false, true, true);

    CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

    Assertions.assertNull(result.getRecipients().getFirst().getDigitalDomicile());
  }

  @Test
  void givenNoPagoPaNoF24ThenPaymentsListEmpty() {
    SendNotificationIngestionFlowFileDTO dto = buildFullDto(true, false, false);
    dto.setPayment(null);
    dto.setPaymentF241(null);

    CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

    Assertions.assertTrue(result.getRecipients().getFirst().getPayments().isEmpty());
  }

  @Test
  void givenPagoPaWithoutAttachmentThenBuildPagoPaStillWorks() {
    SendNotificationIngestionFlowFileDTO dto = buildFullDto(true, true, false);
    dto.setAttachment(null);

    CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

    Assertions.assertNotNull(Objects.requireNonNull(result.getRecipients().getFirst().getPayments()).getFirst().getPagoPa());
    Assertions.assertNull(result.getRecipients().getFirst().getPayments().getFirst().getPagoPa().getAttachment());
  }

  @Test
  void givenIndexOutOfRangeThenPrivateMethodsReturnNull() throws Exception {
    SendNotificationIngestionFlowFileDTO dto = new SendNotificationIngestionFlowFileDTO();

    Method getPaymentByIndex = SendNotificationMapper.class.getDeclaredMethod("getPaymentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
    getPaymentByIndex.setAccessible(true);

    Method getAttachmentByIndex = SendNotificationMapper.class.getDeclaredMethod("getAttachmentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
    getAttachmentByIndex.setAccessible(true);

    Method getF24PaymentByIndex = SendNotificationMapper.class.getDeclaredMethod("getF24PaymentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
    getF24PaymentByIndex.setAccessible(true);

    Method getMetadataAttachmentByIndex = SendNotificationMapper.class.getDeclaredMethod("getMetadataAttachmentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
    getMetadataAttachmentByIndex.setAccessible(true);

    Method getDocumentByIndex = SendNotificationMapper.class.getDeclaredMethod("getDocumentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
    getDocumentByIndex.setAccessible(true);

    int[] invalidIndexes = {6, 7, 10};
    for (int idx : invalidIndexes) {
      Assertions.assertNull(getPaymentByIndex.invoke(mapper, dto, idx));
      Assertions.assertNull(getAttachmentByIndex.invoke(mapper, dto, idx));
      Assertions.assertNull(getF24PaymentByIndex.invoke(mapper, dto, idx));
      Assertions.assertNull(getMetadataAttachmentByIndex.invoke(mapper, dto, idx));
      Assertions.assertNull(getDocumentByIndex.invoke(mapper, dto, idx));
    }
  }


  private SendNotificationIngestionFlowFileDTO buildFullDto(boolean includeDigital, boolean includePagoPa, boolean includeF24) {
    SendNotificationIngestionFlowFileDTO dto = new SendNotificationIngestionFlowFileDTO();
    dto.setOrganizationId(1L);
    dto.setPaProtocolNumber("prot-123");
    dto.setNotificationFeePolicy("DELIVERY_MODE");
    dto.setPhysicalCommunicationType("AR_REGISTERED_LETTER");
    dto.setSenderDenomination("Test Denom");
    dto.setSenderTaxId("ABC123");
    dto.setAmount(new BigDecimal("12.50"));
    dto.setPaymentExpirationDate(LocalDate.now().plusDays(1));
    dto.setTaxonomyCode("TAX001");
    dto.setPaFee(10);
    dto.setVat(20);
    dto.setPagoPaIntMode("NONE");
    dto.setRecipientType("PF");
    dto.setTaxId("TAXID123");
    dto.setDenomination("Mario Rossi");
    dto.setAddress("Via Roma 1");
    dto.setZip("00100");
    dto.setMunicipality("Roma");
    dto.setProvince("RM");

    if (includeDigital) {
      dto.setDigitalDomicileAddress("a@b.it");
      dto.setDigitalDomicileType("PEC");
    }

    if (includePagoPa) {
      MultiValuedMap<String, String> payment = new ArrayListValuedHashMap<>();
      payment.put("paymentNoticeCode_1", "1234567890");
      payment.put("paymentCreditorTaxId_1", "987654321");
      payment.put("paymentApplyCost_1", "true");
      dto.setPayment(payment);

      MultiValuedMap<String, String> attachment = new ArrayListValuedHashMap<>();
      attachment.put("attachmentFileName_1", "file.pdf");
      attachment.put("attachmentDigest_1", "xxxyyyzzz");
      attachment.put("attachmentContentType_1", "application/pdf");
      dto.setAttachment(attachment);
    }

    if (includeF24) {
      MultiValuedMap<String, String> f24Payment = new ArrayListValuedHashMap<>();
      f24Payment.put("paymentF24Title_1", "f24Title");
      f24Payment.put("paymentF24ApplyCost_1", "true");
      dto.setPaymentF241(f24Payment);

      MultiValuedMap<String, String> metadataAttachment = new ArrayListValuedHashMap<>();
      metadataAttachment.put("metadataAttachmentFileName_1", "file.pdf");
      metadataAttachment.put("metadataAttachmentDigest_1", "xxxyyyzzz");
      metadataAttachment.put("metadataAttachmentContentType_1", "application/pdf");
      dto.setMetadataAttachment1(metadataAttachment);
    }

    MultiValuedMap<String, String> document = new ArrayListValuedHashMap<>();
    document.put("documentFileName_1", "doc.pdf");
    document.put("documentDigest_1", "digest123");
    document.put("documentContentType_1", "application/pdf");
    dto.setDocument(document);

    return dto;
  }
}