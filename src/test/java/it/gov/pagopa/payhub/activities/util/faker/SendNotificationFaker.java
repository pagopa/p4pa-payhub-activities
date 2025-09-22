package it.gov.pagopa.payhub.activities.util.faker;


import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.NotificationStatus;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;

public class SendNotificationFaker {
    public static SendNotificationDTO buildSendNotificationDTO() {
        return SendNotificationDTO.builder()
                .sendNotificationId("sendNotificationId")
                .iun("iun")
                .organizationId(1L)
                .payments(Collections.singletonList(SendNotificationPaymentsDTO.builder()
                        .debtPositionId(123L)
                        .notificationDate(OFFSETDATETIME)
                        .navList(Collections.singletonList("nav"))
                        .build()))
                .status(NotificationStatus.ACCEPTED)
                .build();
    }

    public static SendNotificationIngestionFlowFileDTO buildSendNotificationIngestionFlowFileDTO() {
        return SendNotificationIngestionFlowFileDTO.builder()
                .organizationId(1L)
                .paProtocolNumber("prot-123")
                .notificationFeePolicy("DELIVERY_MODE")
                .physicalCommunicationType("AR_REGISTERED_LETTER")
                .senderDenomination("Test Denom")
                .senderTaxId("ABC123")
                .amount(new BigDecimal("12.50"))
                .paymentExpirationDate(LocalDate.now().plusDays(1))
                .taxonomyCode("TAX001")
                .paFee(10)
                .vat(20)
                .pagoPaIntMode("NONE")
                .recipientType("PF")
                .taxId("TAXID123")
                .denomination("Mario Rossi")
                .address("Via Roma 1")
                .zip("00100")
                .municipality("Roma")
                .province("RM")
                .digitalDomicileAddress("a@b.it")
                .digitalDomicileType("PEC")
                .payment(new ArrayListValuedHashMap<>() {{
                    put("paymentNoticeCode_1", "1234567890");
                    put("paymentCreditorTaxId_1", "987654321");
                    put("paymentApplyCost_1", "true");
                }})
                .attachment(new ArrayListValuedHashMap<>() {{
                    put("attachmentFileName_1", "file.pdf");
                    put("attachmentDigest_1", "xxxyyyzzz");
                    put("attachmentContentType_1", "application/pdf");
                }})
                .f24Payment1(new ArrayListValuedHashMap<>() {{
                    put("paymentF24Title_1", "f24Title");
                    put("paymentF24ApplyCost_1", "true");
                }})
                .metadataAttachment1(new ArrayListValuedHashMap<>() {{
                    put("metadataAttachmentFileName_1", "file.pdf");
                    put("metadataAttachmentDigest_1", "xxxyyyzzz");
                    put("metadataAttachmentContentType_1", "application/pdf");
                }})
                .document(new ArrayListValuedHashMap<>() {{
                    put("documentFileName_1", "doc.pdf");
                    put("documentDigest_1", "digest123");
                    put("documentContentType_1", "application/pdf");
                }})
                .build();
    }
}
