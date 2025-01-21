package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationRequestBody;

public class ClassificationRequestMapper {

    private ClassificationRequestMapper() {
    }

    public static ClassificationRequestBody map(Classification classification) {
        if (classification == null) {
            return null;
        }

        return ClassificationRequestBody.builder()
                .classificationId(classification.getClassificationId())
                .organizationId(classification.getOrganizationId())
                .transferId(classification.getTransferId())
                .paymentNotifyId(classification.getPaymentNotifyId())
                .paymentsReportingId(classification.getPaymentsReportingId())
                .treasuryId(classification.getTreasuryId())
                .iuf(classification.getIuf())
                .iud(classification.getIud())
                .iuv(classification.getIuv())
                .iur(classification.getIur())
                .transferIndex(classification.getTransferIndex())
                .label(classification.getLabel())
                .build();
    }
}