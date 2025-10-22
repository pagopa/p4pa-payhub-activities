package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentClassificationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsDetailSemanticKeyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class AssessmentsClassificationActivityImpl implements AssessmentsClassificationActivity {

	private final AssessmentClassificationService assessmentClassificationService;

	public AssessmentsClassificationActivityImpl(AssessmentClassificationService assessmentClassificationService) {
		this.assessmentClassificationService = assessmentClassificationService;
	}

	@Override
	public AssessmentEventDTO classifyAssessment(AssessmentsDetailSemanticKeyDTO assessmentsDetailSemanticKey)  {
		log.info("Start classification of assessments for organizationId: {}, iuv: {}, iud: {}",
				assessmentsDetailSemanticKey.getOrgId(),
				assessmentsDetailSemanticKey.getIuv(),
				assessmentsDetailSemanticKey.getIud()
		);
		return assessmentClassificationService.classifyAssessment(
				assessmentsDetailSemanticKey.getOrgId(),
				assessmentsDetailSemanticKey.getIuv(),
				assessmentsDetailSemanticKey.getIud()
		);
	}
}
