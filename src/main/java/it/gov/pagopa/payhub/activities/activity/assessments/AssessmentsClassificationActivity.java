package it.gov.pagopa.payhub.activities.activity.assessments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsClassificationSemanticKeyDTO;

/**
 * Interface for classifying assessments in the system.
 * This activity is responsible for handling the classification of assessments based on a given organizationId, iuv and iud
 */
@ActivityInterface
public interface AssessmentsClassificationActivity {
	/**
	 * Classify assessments for specified organizationId, iuv and iud
	 *
	 * @param assessmentsClassificationSemanticKeyDTO the DTO containing semantic keys such as organization ID, IUV, IUD,
	 */
	@ActivityMethod
	AssessmentEventDTO classifyAssessment(AssessmentsClassificationSemanticKeyDTO assessmentsClassificationSemanticKeyDTO);

}
