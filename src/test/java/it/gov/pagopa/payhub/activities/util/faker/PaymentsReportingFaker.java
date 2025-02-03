package it.gov.pagopa.payhub.activities.util.faker;


import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelPaymentsReportingEmbedded;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class PaymentsReportingFaker {

    public static PaymentsReporting buildPaymentsReporting() {
        return TestUtils.getPodamFactory().manufacturePojo(PaymentsReporting.class)
                .paymentsReportingId("paymentsReportingId")
                .ingestionFlowFileId(1L)
                .organizationId(1L)
                .iuf("IUF")
                .iuv("IUV")
                .iur("IUR")
                .transferIndex(1)
                .totalAmountCents(100L)
                .pspIdentifier("pspIdentifier")
                .flowDateTime(OffsetDateTime.now())
                .regulationUniqueIdentifier("regulationUniqueIdentifier")
                .regulationDate(LocalDate.now())
                .senderPspType("senderPspType")
                .senderPspCode("senderPspCode")
                .senderPspName("senderPspName")
                .receiverOrganizationType("receiverOrganizationType")
                .receiverOrganizationCode("receiverOrganizationCode")
                .receiverOrganizationName("receiverOrganizationName")
                .totalPayments(1L)
                .amountPaidCents(1L)
                .paymentOutcomeCode("paymentOutcomeCode")
                .payDate(LocalDate.now())
                .acquiringDate(LocalDate.now())
                .bicCodePouringBank("bicCodePouringBank");
    }

    public static CollectionModelPaymentsReporting buildCollectionModelPaymentsReporting() {
        PagedModelPaymentsReportingEmbedded embedded = PagedModelPaymentsReportingEmbedded.builder()
                .paymentsReportings(List.of(buildPaymentsReporting()))
                .build();

        return CollectionModelPaymentsReporting.builder()
                .embedded(embedded)
                .build();
    }

}
