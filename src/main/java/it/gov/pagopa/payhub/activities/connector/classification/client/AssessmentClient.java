package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Lazy
@Service
@Slf4j
public class AssessmentClient {

  private final ClassificationApisHolder classificationApisHolder;

  public AssessmentClient(ClassificationApisHolder classificationApisHolder) {
    this.classificationApisHolder = classificationApisHolder;
  }

  public List<Assessments> createAssessments(Long receiptId, String accessToken) {
    return classificationApisHolder.getAssessmentsControllerApi(accessToken)
        .createAssessmentByReceiptId(receiptId);
  }

  public Assessments findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(Long organizationId, String debtPositionTypeOrgCode, String assessmentName, String accessToken) {
    try {
      return classificationApisHolder.getAssessmentsSearchControllerApi(accessToken)
              .crudAssessmentsFindByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(
                      organizationId, debtPositionTypeOrgCode, assessmentName);
    } catch (HttpClientErrorException.NotFound e) {
      log.info("Assessment not found: organizationId: {}, debtPositionTypeOrgCode: {}, assessmentName: {}", organizationId, debtPositionTypeOrgCode, assessmentName);
      return null;
    }
  }

  public Assessments createAssessment(AssessmentsRequestBody assessments, String accessToken) {
    return classificationApisHolder.getAssessmentsEntityControllerApi(accessToken)
        .crudCreateAssessments(assessments);
  }

  public Assessments findAssessment(Long assessmentId, String accessToken) {
    return classificationApisHolder.getAssessmentsEntityControllerApi(accessToken)
            .crudGetAssessments(String.valueOf(assessmentId));
  }


}
