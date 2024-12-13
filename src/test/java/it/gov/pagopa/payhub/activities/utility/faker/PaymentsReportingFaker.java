package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;

public class PaymentsReportingFaker {

    public static PaymentsReportingDTO buildPaymentsReportingDTO(){
        return PaymentsReportingDTO.builder()
                .flowIdentifierCode("FLOW_IDENTIFIER_CODE")
                .organizationId(1L)
                .build();
    }
}
