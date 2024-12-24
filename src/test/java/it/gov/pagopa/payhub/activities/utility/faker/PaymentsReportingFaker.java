package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;

public class PaymentsReportingFaker {

    public static PaymentsReportingDTO buildClassifyResultDTO(){
        return PaymentsReportingDTO.builder()
                .organizationId(1L)
                .creditorReferenceId("IUV")
                .regulationUniqueIdentifier("IUR")
                .transferIndex(1)
                .build();
    }
}
