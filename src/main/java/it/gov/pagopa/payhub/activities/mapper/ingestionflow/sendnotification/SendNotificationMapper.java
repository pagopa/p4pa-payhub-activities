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
import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
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
    return Stream.of(
            new AbstractMap.SimpleEntry<>(dto.getPayment(), dto.getAttachment()),
            new AbstractMap.SimpleEntry<>(dto.getPayment2(), dto.getAttachment2()),
            new AbstractMap.SimpleEntry<>(dto.getPayment3(), dto.getAttachment3()),
            new AbstractMap.SimpleEntry<>(dto.getPayment4(), dto.getAttachment4()),
            new AbstractMap.SimpleEntry<>(dto.getPayment5(), dto.getAttachment5())
        )
        .filter(entry -> entry.getKey() != null && entry.getValue() != null)
        .map(entry -> getPayment(entry.getKey(), entry.getValue()))
        .toList();

  }

  private Payment getPayment(MultiValuedMap<String, String> paymentMap, MultiValuedMap<String, String> attachmentMap) {
    PagoPa pagoPa = new PagoPa();
    pagoPa.setNoticeCode(getRequiredValue(paymentMap, "paymentNoticeCode"));
    pagoPa.setCreditorTaxId(getRequiredValue(paymentMap, "paymentCreditorTaxId"));
    pagoPa.setApplyCost(Boolean.valueOf(getRequiredValue(paymentMap, "paymentApplyCost")));

    if (attachmentMap != null) {
      pagoPa.setAttachment(buildAttachment(attachmentMap));
    }

    return Payment.builder()
        .pagoPa(pagoPa)
        .build();
  }


  private Attachment buildAttachment(MultiValuedMap<String, String> map) {
    return Attachment.builder()
        .fileName(getRequiredValue(map, "attachmentFileName"))
        .digest(getRequiredValue(map, "attachmentDigest"))
        .contentType(getRequiredValue(map, "attachmentContentType"))
        .build();
  }

  private List<Document> buildDocuments(SendNotificationIngestionFlowFileDTO dto) {
    return Stream.of(
            dto.getDocument(),
            dto.getDocument2(),
            dto.getDocument3(),
            dto.getDocument4(),
            dto.getDocument5()
        )
        .filter(Objects::nonNull)
        .map(this::getDocument)
        .filter(Objects::nonNull)
        .toList();
  }

  private Document getDocument(MultiValuedMap<String, String> map) {
    return Document.builder()
        .fileName(getRequiredValue(map, "documentFileName"))
        .digest(getRequiredValue(map, "documentDigest"))
        .contentType(getRequiredValue(map, "documentContentType"))
        .build();
  }

  private String getRequiredValue(MultiValuedMap<String, String> map, String key) {
    return Optional.ofNullable(map.get(key))
        .flatMap(list -> list.stream().findFirst())
        .orElseThrow(() -> new IllegalArgumentException("Missing required value for key: " + key));
  }

}
