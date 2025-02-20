package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.pagopa_api.xsd.common_types.v1_0.CtMapEntry;
import it.gov.pagopa.pagopa_api.xsd.common_types.v1_0.CtMetadata;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtReceiptV2;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtSubject;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtTransferPAReceiptV2;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptTransferDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceiptMapper {

  public ReceiptWithAdditionalNodeDataDTO map(PaSendRTV2Request paSendRTV2Request) {
    CtReceiptV2 rec = paSendRTV2Request.getReceipt();

    return new ReceiptWithAdditionalNodeDataDTO()
      .receiptId(null)
      .ingestionFlowFileId(null)
      .receiptOrigin(ReceiptWithAdditionalNodeDataDTO.ReceiptOriginEnum.PAGOPA)
      .paymentReceiptId(rec.getReceiptId())
      .noticeNumber(rec.getNoticeNumber())
      .paymentNote(rec.getPaymentNote())
      .orgFiscalCode(rec.getFiscalCode())
      .outcome(rec.getOutcome().value())
      .creditorReferenceId(rec.getCreditorReferenceId())
      .paymentAmountCents(Utilities.bigDecimalEuroToLongCentsAmount(rec.getPaymentAmount()))
      .description(rec.getDescription())
      .companyName(rec.getCompanyName())
      .officeName(rec.getOfficeName())
      .idPsp(rec.getIdPSP())
      .pspFiscalCode(rec.getPspFiscalCode())
      .pspPartitaIva(rec.getPspPartitaIVA())
      .pspCompanyName(rec.getPSPCompanyName())
      .idChannel(rec.getIdChannel())
      .channelDescription(rec.getChannelDescription())
      .paymentMethod(rec.getPaymentMethod())
      .feeCents(Utilities.bigDecimalEuroToLongCentsAmount(rec.getFee()))
      .paymentDateTime(Utilities.toOffsetDateTime(rec.getPaymentDateTime()))
      .applicationDate(Utilities.toOffsetDateTime(rec.getApplicationDate()))
      .transferDate(Utilities.toOffsetDateTime(rec.getTransferDate()))
      .standin(rec.isStandIn())
      .debtor(map(rec.getDebtor()))
      .payer(map(rec.getPayer()))
      .creationDate(null)
      .updateDate(null)
      .transfers(rec.getTransferList().getTransfers().stream().map(this::map).toList())
      .metadata(map(rec.getMetadata()));
  }

  public PersonDTO map(CtSubject subject) {
    return new PersonDTO()
      .entityType(PersonDTO.EntityTypeEnum.fromValue(subject.getUniqueIdentifier().getEntityUniqueIdentifierType().value()))
      .fiscalCode(subject.getUniqueIdentifier().getEntityUniqueIdentifierValue())
      .fullName(subject.getFullName())
      .email(subject.getEMail())
      .nation(subject.getCountry())
      .province(subject.getStateProvinceRegion())
      .location(subject.getCity())
      .postalCode(subject.getPostalCode())
      .address(subject.getStreetName())
      .civic(subject.getCivicNumber());
  }

  public ReceiptTransferDTO map(CtTransferPAReceiptV2 transfer) {
    return new ReceiptTransferDTO()
      .idTransfer(transfer.getIdTransfer())
      .transferAmountCents(Utilities.bigDecimalEuroToLongCentsAmount(transfer.getTransferAmount()))
      .fiscalCodePA(transfer.getFiscalCodePA())
      .companyName(transfer.getCompanyName())
      .mbdAttachment(transfer.getMBDAttachment())
      .iban(transfer.getIBAN())
      .remittanceInformation(transfer.getRemittanceInformation())
      .transferCategory(transfer.getTransferCategory())
      .metadata(map(transfer.getMetadata()));
  }

  public Map<String, String> map(CtMetadata metadata) {
    return metadata == null ? null : metadata.getMapEntries().stream().collect(Collectors.toUnmodifiableMap(CtMapEntry::getKey, CtMapEntry::getValue));
  }
}
