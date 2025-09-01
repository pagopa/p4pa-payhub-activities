package it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.Address;
import it.gov.pagopa.pu.sendnotification.dto.generated.Attachment;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.Document;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationDigitalAddress;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationFeePolicyEnum;
import it.gov.pagopa.pu.sendnotification.dto.generated.PagoPa;
import it.gov.pagopa.pu.sendnotification.dto.generated.PagoPaIntModeEnum;
import it.gov.pagopa.pu.sendnotification.dto.generated.Payment;
import it.gov.pagopa.pu.sendnotification.dto.generated.PhysicalCommunicationTypeEnum;

import it.gov.pagopa.pu.sendnotification.dto.generated.Recipient;
import it.gov.pagopa.pu.sendnotification.dto.generated.RecipientTypeEnum;
import it.gov.pagopa.pu.sendnotification.dto.generated.TypeEnum;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class SendNotificationMapper {

  public CreateNotificationRequest buildCreateNotificationRequest(SendNotificationIngestionFlowFileDTO sendFile) {
    CreateNotificationRequest request = new CreateNotificationRequest();
    request.setOrganizationId(sendFile.getOrganizationId());
    request.setPaProtocolNumber(sendFile.getPaProtocolNumber());
    request.setNotificationFeePolicy(NotificationFeePolicyEnum.valueOf(sendFile.getNotificationFeePolicy()));
    request.setPhysicalCommunicationType(PhysicalCommunicationTypeEnum.valueOf(sendFile.getPhysicalCommunicationType()));
    request.setSenderDenomination(sendFile.getSenderDenomination());
    request.setSenderTaxId(sendFile.getSenderTaxId());
    request.setAmount(sendFile.getAmount());
    request.setPaymentExpirationDate(sendFile.getPaymentExpirationDate());
    request.setTaxonomyCode(sendFile.getTaxonomyCode());
    request.setPaFee(sendFile.getPaFee());
    request.setVat(sendFile.getVat());
    request.setPagoPaIntMode(PagoPaIntModeEnum.valueOf(sendFile.getPagoPaIntMode()));

    Recipient recipient = new Recipient();
    recipient.setRecipientType(RecipientTypeEnum.valueOf(sendFile.getRecipientType()));
    recipient.setTaxId(sendFile.getTaxId());
    recipient.setDenomination(sendFile.getDenomination());

    Address address = new Address();
    address.setAddress(sendFile.getAddress());
    address.setZip(sendFile.getZip());
    address.setMunicipality(sendFile.getMunicipality());
    address.setProvince(sendFile.getProvince());
    recipient.setPhysicalAddress(address);

    if(sendFile.getDigitalDomicileAddress()!=null)
    {
      NotificationDigitalAddress digitalAddress = new NotificationDigitalAddress();
      digitalAddress.setAddress(sendFile.getDigitalDomicileAddress());
      digitalAddress.setType(TypeEnum.valueOf(sendFile.getDigitalDomicileType()));
      recipient.setDigitalDomicile(digitalAddress);
    }
    recipient.setPayments(buildPayments(sendFile));
    request.setDocuments(buildDocuments(sendFile));
    request.setRecipients(List.of(recipient));
    return request;
  }

  private List<Payment> buildPayments(SendNotificationIngestionFlowFileDTO dto) {
    return IntStream.rangeClosed(1, 5)
        .mapToObj(i -> new IndexedEntry(i, getPaymentByIndex(dto, i), getAttachmentByIndex(dto, i)))
        .filter(entry -> entry.paymentMap() != null && entry.attachmentMap() != null)
        .map(entry -> getPayment(entry.paymentMap(), entry.attachmentMap(), entry.index()))
        .toList();
  }

  private Payment getPayment(MultiValuedMap<String, String> paymentMap, MultiValuedMap<String, String> attachmentMap, int index) {
    PagoPa pagoPa = new PagoPa();
    pagoPa.setNoticeCode(getFirstValue(paymentMap, "paymentNoticeCode_"+index));
    pagoPa.setCreditorTaxId(getFirstValue(paymentMap, "paymentCreditorTaxId_"+index));
    pagoPa.setApplyCost(Boolean.valueOf(getFirstValue(paymentMap, "paymentApplyCost_"+index)));

    if (attachmentMap != null) {
      pagoPa.setAttachment(buildAttachment(attachmentMap, index));
    }

    return Payment.builder()
        .pagoPa(pagoPa)
        .build();
  }


  private Attachment buildAttachment(MultiValuedMap<String, String> map, int index) {
    return Attachment.builder()
        .fileName(getFirstValue(map, "attachmentFileName_"+index))
        .digest(getFirstValue(map, "attachmentDigest_"+index))
        .contentType(getFirstValue(map, "attachmentContentType_"+index))
        .build();
  }


  private List<Document> buildDocuments(SendNotificationIngestionFlowFileDTO dto) {
    return IntStream.rangeClosed(1, 5)
        .mapToObj(i -> getDocument(dto, i))
        .filter(Objects::nonNull)
        .toList();
  }

  private Document getDocument(SendNotificationIngestionFlowFileDTO dto, int index) {
    MultiValuedMap<String, String> map = getDocumentByIndex(dto, index);

    if(map!=null && !map.isEmpty()){
      return Document.builder()
          .fileName(getFirstValue(map, "documentFileName_"+index))
          .digest(getFirstValue(map, "documentDigest_"+index))
          .contentType(getFirstValue(map, "documentContentType_"+index))
          .build();
      } else
        return null;
  }

  private String getFirstValue(MultiValuedMap<String, String> map, String key) {
    return Optional.ofNullable(map.get(key))
        .flatMap(list -> list.stream().findFirst())
        .orElseThrow(() -> new IllegalArgumentException("Missing required value for key: " + key));
  }

  private MultiValuedMap<String, String> getPaymentByIndex(SendNotificationIngestionFlowFileDTO dto, int index) {
    return switch (index) {
      case 1 -> dto.getPayment();
      case 2 -> dto.getPayment2();
      case 3 -> dto.getPayment3();
      case 4 -> dto.getPayment4();
      case 5 -> dto.getPayment5();
      default -> null;
    };
  }

  private MultiValuedMap<String, String> getAttachmentByIndex(SendNotificationIngestionFlowFileDTO dto, int index) {
    return switch (index) {
      case 1 -> dto.getAttachment();
      case 2 -> dto.getAttachment2();
      case 3 -> dto.getAttachment3();
      case 4 -> dto.getAttachment4();
      case 5 -> dto.getAttachment5();
      default -> null;
    };
  }

  private MultiValuedMap<String, String> getDocumentByIndex(SendNotificationIngestionFlowFileDTO dto, int index) {
    return switch (index) {
      case 1 -> dto.getDocument();
      case 2 -> dto.getDocument2();
      case 3 -> dto.getDocument3();
      case 4 -> dto.getDocument4();
      case 5 -> dto.getDocument5();
      default -> null;
    };
  }

  private record IndexedEntry(int index, MultiValuedMap<String, String> paymentMap, MultiValuedMap<String, String> attachmentMap) {}
}
