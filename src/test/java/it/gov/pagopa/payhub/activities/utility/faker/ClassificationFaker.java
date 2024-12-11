package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;

import java.time.LocalDate;

public class ClassificationFaker {

    public static PaymentsClassificationDTO buildPaymentsClassification(){
        return PaymentsClassificationDTO.builder()
            .transferId(1L)
            .classificationCode("CLASSIFICATION_CODE")
            .creationDate(LocalDate.now())
            .organizationId(1L)
            .paymentNotifyId(1L)
            .paymentReportingId(1L)
            .treasuryId(1L)
            .build();
    }

}
