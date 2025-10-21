package it.gov.pagopa.payhub.activities.activity.assessments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;

/**
 * Interface for classifying assessments in the system.
 * This activity is responsible for handling the classification of assessments based on a given organizationId, iuv and iud
 */
@ActivityInterface
public interface AssessmentsClassificationActivity {
	/**
	 * Classify assessments for specified organizationId, iuv and iud
	 *
	 * @param organizationId the unique identifier of the organization which creates the assessment
	 * @param iuv the unique identifier of the transfer related to the assessment
	 * @param iud the unique identifier of the debt position related to the assessment
	 */
	@ActivityMethod
	AssessmentEventDTO classifyAssessments(Long organizationId, String iuv, String iud);

}
