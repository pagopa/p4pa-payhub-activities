package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;

import static it.gov.pagopa.payhub.activities.utility.TestUtils.DATE;

public class PaymentsReportingFaker {

    public static PaymentsReportingDTO buildPaymentsReportingDTO(){
        return PaymentsReportingDTO.builder()
                .flowIdentifierCode("FLOW_IDENTIFIER_CODE")
                .organizationId(1L)
                .build();
    }
}
