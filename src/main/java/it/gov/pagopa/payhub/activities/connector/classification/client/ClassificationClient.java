package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelClassification;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Lazy
@Service
public class ClassificationClient {
    private final ClassificationApisHolder classificationApisHolder;

    public ClassificationClient(ClassificationApisHolder classificationApisHolder) {
        this.classificationApisHolder = classificationApisHolder;

    }
    public Integer saveAll(List<Classification> classificationList, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .saveAll2(classificationList);
    }

    public Integer deleteByOrganizationIdAndIufAndLabel(Long organizationId, String iuf, ClassificationsEnum classification, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification);
    }

    public Integer deleteByOrganizationIdAndIuvAndIurAndTransferIndex(Long organizationId, String iuv,String iur,  int transferIndex, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex);
    }

    public Integer deleteByOrganizationIdAndIudAndLabel(Long organizationId, String iud, ClassificationsEnum classification, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification);
    }

    public Integer deleteByOrganizationIdAndTreasuryId(Long organizationId, String treasuryId, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndTreasuryId(organizationId, treasuryId);
    }

    public Integer deleteByOrganizationIdAndIuvAndIurAndTransferIndexAndLabelNot(Long organizationId, String iuv,String iur,  int transferIndex, ClassificationsEnum label, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
            .deleteByOrganizationIdAndIuvAndIurAndTransferIndexAndLabelNot(organizationId, iuv, iur, transferIndex, label);
    }

    public Integer deleteDuplicates(Long organizationId, String iuv, int transferIndex,
        Long receiptPaymentAmount, String receiptOrgFiscalCode,
        ClassificationsEnum label, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
            .deleteDuplicates(organizationId, iuv, transferIndex, receiptPaymentAmount, receiptOrgFiscalCode, label);
    }

    public List<Classification> findAllByOrganizationIdAndIuvAndIud(Long organizationId, String iuv, String iud, String accessToken) {
        CollectionModelClassification collectionModelClassification = classificationApisHolder.getClassificationSearchControllerApi(accessToken)
                .crudClassificationsFindAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud);
        return collectionModelClassification.getEmbedded() == null ||
                collectionModelClassification.getEmbedded().getClassifications() == null?
                Collections.emptyList() :
                collectionModelClassification.getEmbedded().getClassifications();
    }
}
