package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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

}
