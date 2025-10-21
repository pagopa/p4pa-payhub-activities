package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentClassificationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
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
	public AssessmentEventDTO classifyAssessment(Long organizationId, String iuv, String iud) {
		log.info("Start classification of assessments for organizationId: {}, iuv: {}, iud: {}", organizationId, iuv, iud);
		return assessmentClassificationService.classifyAssessment(organizationId, iuv, iud);
	}
}
