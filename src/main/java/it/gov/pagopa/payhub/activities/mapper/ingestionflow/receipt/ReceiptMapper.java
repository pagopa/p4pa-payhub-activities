package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.pagopa_api.xsd.common_types.v1_0.CtMapEntry;
import it.gov.pagopa.pagopa_api.xsd.common_types.v1_0.CtMetadata;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.service.receipt.RtFileHandlerService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtReceiptV2;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtSubject;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtTransferPAReceiptV2;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Lazy
public class ReceiptMapper {

  private final RtFileHandlerService rtFileHandlerService;
  private final OrganizationService organizationService;

  public ReceiptMapper(RtFileHandlerService rtFileHandlerService,
    OrganizationService organizationService) {
    this.rtFileHandlerService = rtFileHandlerService;
    this.organizationService = organizationService;
  }

  public ReceiptWithAdditionalNodeDataDTO map(IngestionFlowFile ingestionFlowFile, PaSendRTV2Request paSendRTV2Request) {
    Long organizationId = ingestionFlowFile.getOrganizationId();
    CtReceiptV2 rec = paSendRTV2Request.getReceipt();
     organizationService.getOrganizationById(organizationId);
    Organization org = organizationService.getOrganizationById(organizationId)
        .orElseThrow(() -> new OrganizationNotFoundException("Organization with id "+organizationId+" not found."));

    return new ReceiptWithAdditionalNodeDataDTO()
      .ingestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId())
      .receiptOrigin(ReceiptOriginType.RECEIPT_PAGOPA)
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
      .sourceFlowName(org.getIpaCode()+"_IMPORT-DOVUTO")
      .feeCents(Utilities.bigDecimalEuroToLongCentsAmount(rec.getFee()))
      .paymentDateTime(Utilities.toOffsetDateTime(rec.getPaymentDateTime()))
      .applicationDate(Utilities.toOffsetDateTime(rec.getApplicationDate()))
      .transferDate(Utilities.toOffsetDateTime(rec.getTransferDate()))
      .standin(rec.isStandIn())
      .debtor(map(rec.getDebtor()))
      .payer(Optional.ofNullable(rec.getPayer()).map(this::map).orElse(null))
      .transfers(rec.getTransferList().getTransfers().stream().map(this::map).toList())
      .rtFilePath(rtFileHandlerService.store(organizationId, rec, ingestionFlowFile.getFileName()))
      .metadata(map(rec.getMetadata()));
  }

  private PersonDTO map(CtSubject subject) {
    return new PersonDTO()
      .entityType(EntityTypeEnum.fromValue(subject.getUniqueIdentifier().getEntityUniqueIdentifierType().value()))
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

  private ReceiptTransferDTO map(CtTransferPAReceiptV2 transfer) {
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

  private Map<String, String> map(CtMetadata metadata) {
    return metadata == null ? null : metadata.getMapEntries().stream().collect(Collectors.toUnmodifiableMap(CtMapEntry::getKey, CtMapEntry::getValue));
  }
}
