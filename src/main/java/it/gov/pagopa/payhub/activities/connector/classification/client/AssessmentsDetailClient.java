package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelAssessmentsDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class AssessmentsDetailClient {

  private final ClassificationApisHolder classificationApisHolder;

  public AssessmentsDetailClient(ClassificationApisHolder classificationApisHolder) {
    this.classificationApisHolder = classificationApisHolder;
  }

  public AssessmentsDetail createAssessmentDetail(AssessmentsDetailRequestBody assessmentsDetailRequestBody, String accessToken) {
    return classificationApisHolder.getAssessmentsDetailEntityControllerApi(accessToken)
        .crudCreateAssessmentsdetail(assessmentsDetailRequestBody);
  }

  public CollectionModelAssessmentsDetail findAssessmentsDetailByOrganizationIdAndIuvAndIud(Long organizationId, String iuv, String iud, String accessToken) {
    return classificationApisHolder.getAssessmentsDetailSearchControllerApi(accessToken)
            .crudAssessmentsDetailsFindAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud);
  }

  public AssessmentsDetail updateAssessmentsDetail(Long assessmentDetailId, AssessmentsDetailRequestBody updateRequest, String accessToken) {
    return classificationApisHolder.getAssessmentsDetailEntityControllerApi(accessToken)
            .crudUpdateAssessmentsdetail(String.valueOf(assessmentDetailId), updateRequest);
  }


}
