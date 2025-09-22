package it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

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

        if (sendFile.getDigitalDomicileAddress() != null) {
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
                .mapToObj(i -> {
                    MultiValuedMap<String, String> pagoPaMap = getPaymentByIndex(dto, i);
                    MultiValuedMap<String, String> attachmentMap = getAttachmentByIndex(dto, i);
                    MultiValuedMap<String, String> f24Map = getF24PaymentByIndex(dto, i);
                    MultiValuedMap<String, String> metadataAttachmentMap = getMetadataAttachmentByIndex(dto, i);

                    boolean hasPagoPa = pagoPaMap != null && !pagoPaMap.isEmpty();
                    boolean hasF24 = f24Map != null && !f24Map.isEmpty();

                    if (!hasPagoPa && !hasF24) {
                        return null;
                    }

                    return (Payment) Payment.builder()
                            .pagoPa(hasPagoPa ? buildPagoPa(pagoPaMap, attachmentMap, i) : null)
                            .f24(hasF24 ? buildF24(f24Map, metadataAttachmentMap, i) : null)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private PagoPa buildPagoPa(MultiValuedMap<String, String> paymentMap, MultiValuedMap<String, String> attachmentMap, int index) {
        PagoPa pagoPa = new PagoPa();
        pagoPa.setNoticeCode(getFirstValue(paymentMap, "paymentNoticeCode_" + index));
        pagoPa.setCreditorTaxId(getFirstValue(paymentMap, "paymentCreditorTaxId_" + index));
        pagoPa.setApplyCost(Boolean.valueOf(getFirstValue(paymentMap, "paymentApplyCost_" + index)));

        if (attachmentMap != null) {
            pagoPa.setAttachment(buildAttachment(attachmentMap, index));
        }
        return pagoPa;
    }

    private F24Payment buildF24(MultiValuedMap<String, String> paymentMap, MultiValuedMap<String, String> metadataAttachmentMap, int index) {
        F24Payment f24 = new F24Payment();
        f24.setTitle(getFirstValue(paymentMap, "f24PaymentTitle_" + index));
        f24.setApplyCost(Boolean.valueOf(getFirstValue(paymentMap, "f24PaymentApplyCost_" + index)));

        if (metadataAttachmentMap != null) {
            f24.setMetadataAttachment(buildMetadataAttachment(metadataAttachmentMap, index));
        }
        return f24;
    }

    private Attachment buildAttachment(MultiValuedMap<String, String> map, int index) {
        return Attachment.builder()
                .fileName(getFirstValue(map, "attachmentFileName_" + index))
                .digest(getFirstValue(map, "attachmentDigest_" + index))
                .contentType(getFirstValue(map, "attachmentContentType_" + index))
                .build();
    }

    private Attachment buildMetadataAttachment(MultiValuedMap<String, String> map, int index) {
        return Attachment.builder()
                .fileName(getFirstValue(map, "metadataAttachmentFileName_" + index))
                .digest(getFirstValue(map, "metadataAttachmentDigest_" + index))
                .contentType(getFirstValue(map, "metadataAttachmentContentType_" + index))
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

        if (map != null && !map.isEmpty()) {
            return Document.builder()
                    .fileName(getFirstValue(map, "documentFileName_" + index))
                    .digest(getFirstValue(map, "documentDigest_" + index))
                    .contentType(getFirstValue(map, "documentContentType_" + index))
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

    private MultiValuedMap<String, String> getF24PaymentByIndex(SendNotificationIngestionFlowFileDTO dto, int index) {
        return switch (index) {
            case 1 -> dto.getF24Payment1();
            case 2 -> dto.getF24Payment2();
            case 3 -> dto.getF24Payment3();
            case 4 -> dto.getF24Payment4();
            case 5 -> dto.getF24Payment5();
            default -> null;
        };
    }

    private MultiValuedMap<String, String> getMetadataAttachmentByIndex(SendNotificationIngestionFlowFileDTO dto, int index) {
        return switch (index) {
            case 1 -> dto.getMetadataAttachment1();
            case 2 -> dto.getMetadataAttachment2();
            case 3 -> dto.getMetadataAttachment3();
            case 4 -> dto.getMetadataAttachment4();
            case 5 -> dto.getMetadataAttachment5();
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

}
