package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;

/**
 * This interface provides methods for classifying assessments in the system.
 */
public interface AssessmentClassificationService {

	/**
	 * Classify assessments for specified organizationId, iuv and iud
	 *
	 * @param organizationId the unique identifier of the organization which creates the assessment
	 * @param iuv the unique identifier of the transfer related to the assessment
	 * @param iud the unique identifier of the debt position related to the assessment
	 * @return Assessments object with list of classified AssessmentDetails
	 */
	AssessmentEventDTO classifyAssessment(Long organizationId, String iuv, String iud);
}
