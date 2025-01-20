package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.Classification;

public class ClassificationFaker {

    public static Classification buildClassificationDTO(){
        return Classification.builder()
                .organizationId(1L)
                .treasuryId("treasuryId")
                .iuf("IUF")
                .label(String.valueOf(ClassificationsEnum.TES_NO_MATCH))
                .build();
    }

    public static Classification buildFullClassificationDTO(){
        return Classification.builder()
                .classificationId(1L)
                .organizationId(2L)
                .transferId(3L)
                .paymentNotifyId(4L)
                .paymentsReportingId("paymentsReportingId")
                .treasuryId("treasuryId")
                .iuf("IUF")
                .iud("IUD")
                .iuv("IUV")
                .iur("IUR")
                .transferIndex(7)
                .label(String.valueOf(ClassificationsEnum.TES_NO_MATCH))
                .build();
    }

}