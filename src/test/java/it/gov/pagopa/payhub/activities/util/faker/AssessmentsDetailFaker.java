package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationLabel;

import java.time.OffsetDateTime;

public class AssessmentsDetailFaker {

    private AssessmentsDetailFaker() {
    }

    public static AssessmentsDetail buildAssessmentsDetail() {
        return AssessmentsDetail.builder()
                .creationDate(OffsetDateTime.now())
                .updateDate(OffsetDateTime.now())
                .updateOperatorExternalId("operator-123")
                .updateTraceId("trace-abc")
                .assessmentDetailId(1001L)
                .assessmentId(2002L)
                .organizationId(3003L)
                .debtPositionTypeOrgCode("DPT001")
                .debtPositionTypeOrgId(4004L)
                .iuv("IUV001")
                .iud("IUD001")
                .iur("IUR001")
                .debtorFiscalCodeHash("ABCDEF1234567890".getBytes())
                .paymentDateTime(OffsetDateTime.now())
                .officeCode("OFF001")
                .officeDescription("OFF001Description")
                .sectionCode("SEC001")
                .sectionDescription("SEC001Description")
                .assessmentCode("ASMT001")
                .assessmentDescription("ASMT001Description")
                .amountCents(12345L)
                .amountSubmitted(true)
                .receiptId(4004L)
                .classificationLabel(ClassificationLabel.CASHED)
                .dateTreasury(OffsetDateTime.now())
                .dateReporting(OffsetDateTime.now())
                .dateReceipt(OffsetDateTime.now())
                .build();
    }

}
