package it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.Address;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.Document;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationDigitalAddress;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationFeePolicyEnum;
import it.gov.pagopa.pu.sendnotification.dto.generated.PagoPaIntModeEnum;
import it.gov.pagopa.pu.sendnotification.dto.generated.Payment;
import it.gov.pagopa.pu.sendnotification.dto.generated.PhysicalCommunicationTypeEnum;

import it.gov.pagopa.pu.sendnotification.dto.generated.Recipient;
import it.gov.pagopa.pu.sendnotification.dto.generated.RecipientTypeEnum;
import it.gov.pagopa.pu.sendnotification.dto.generated.TypeEnum;
import java.util.List;
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

    recipient.setPayments(buildPayments());
    request.setDocuments(buildDocuments());
    request.setRecipients(List.of(recipient));
    return request;
  }

  private List<Payment> buildPayments() {
    return List.of(null);
  }

  private List<Document> buildDocuments() {
    return List.of(null);
  }
}
