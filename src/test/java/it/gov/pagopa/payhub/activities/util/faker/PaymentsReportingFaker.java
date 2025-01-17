package it.gov.pagopa.payhub.activities.util.faker;


import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelPaymentsReportingEmbedded;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;

import java.util.List;

public class PaymentsReportingFaker {

    public static PaymentsReporting buildPaymentsReporting() {
        return PaymentsReporting.builder()
                .paymentsReportingId("paymentsReportingId")
                .organizationId(1L)
                .iuf("IUF")
                .iuv("IUV")
                .iur("IUR")
                .transferIndex(1)
                .totalAmountCents(100L)
                .build();
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
