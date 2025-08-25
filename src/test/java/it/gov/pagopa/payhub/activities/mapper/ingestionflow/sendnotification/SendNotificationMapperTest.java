package it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;

import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendNotificationMapperTest {

  private SendNotificationMapper mapper;

  @BeforeEach
  void setup() {
    mapper = new SendNotificationMapper();
  }

  @Test
  void givenMapThenOk() {
    // Given
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
    // digital domicile
    dto.setDigitalDomicileAddress("a@b.it");
    dto.setDigitalDomicileType("PEC");
    // Payment and attachment
    MultiValuedMap<String, String> payment = new ArrayListValuedHashMap<>();
    payment.put("paymentNoticeCode_1", "1234567890");
    payment.put("paymentCreditorTaxId_1", "987654321");
    payment.put("paymentApplyCost_1", "true");
    MultiValuedMap<String, String> attachment = new ArrayListValuedHashMap<>();
    attachment.put("attachmentFileName_1", "file.pdf");
    attachment.put("attachmentDigest_1", "xxxyyyzzz");
    attachment.put("attachmentContentType_1", "application/pdf");
    dto.setPayment(payment);
    dto.setAttachment(attachment);
    // Document
    MultiValuedMap<String, String> document = new ArrayListValuedHashMap<>();
    document.put("documentFileName_1", "doc.pdf");
    document.put("documentDigest_1", "digest123");
    document.put("documentContentType_1", "application/pdf");
    dto.setDocument(document);
    // When
    CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);
    // Then
    Assertions.assertNotNull(result);
    checkNotNullFields(result);
  }
}