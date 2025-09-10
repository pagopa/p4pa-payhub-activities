package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.payhub.activities.connector.classification.mapper.ClassificationRequestMapper;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class ClassificationClient {

    private final ClassificationApisHolder classificationApisHolder;
    private final ClassificationRequestMapper mapper;

    public ClassificationClient(ClassificationApisHolder classificationApisHolder, ClassificationRequestMapper mapper) {
        this.classificationApisHolder = classificationApisHolder;
        this.mapper = mapper;
    }
    public Integer saveAll(List<Classification> classificationList, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .saveAll2(classificationList);
    }

    public Long deleteByOrganizationIdAndIufAndLabel(Long organizationId, String iuf, ClassificationsEnum classification, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification);
    }

    public Long deleteByOrganizationIdAndIuvAndIurAndTransferIndex(Long organizationId, String iuv,String iur,  int transferIndex, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex);
    }

    public Long deleteByOrganizationIdAndIudAndLabel(Long organizationId, String iud, ClassificationsEnum classification, String accessToken) {
        return classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                .deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification);
    }
}
