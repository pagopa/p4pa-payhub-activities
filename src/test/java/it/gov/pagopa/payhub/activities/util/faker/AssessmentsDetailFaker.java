package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;

public class AssessmentsDetailFaker {

    private AssessmentsDetailFaker() {
    }

    public static AssessmentsDetailRequestBody buildAssessmentsDetail() {
        return AssessmentsDetailRequestBody.builder()
                .creationDate(java.time.OffsetDateTime.now())
                .updateDate(java.time.OffsetDateTime.now())
                .updateOperatorExternalId("operator-123")
                .updateTraceId("trace-abc")
                .assessmentDetailId(1001L)
                .assessmentId(2002L)
                .organizationId(3003L)
                .debtPositionTypeOrgCode("DPT001")
                .iuv("IUV001")
                .iud("IUD001")
                .iur("IUR001")
                .debtorFiscalCodeHash("ABCDEF1234567890".getBytes())
                .paymentDateTime(java.time.OffsetDateTime.now())
                .officeCode("OFF001")
                .sectionCode("SEC001")
                .assessmentCode("ASMT001")
                .amountCents(12345L)
                .amountSubmitted(true)
                .receiptId(4004L)
                .build();
    }
}
