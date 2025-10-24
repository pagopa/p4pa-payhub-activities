package it.gov.pagopa.payhub.activities.service.classifications.assessments;

import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsClassificationSemanticKeyDTO;

/**
 * This interface provides methods for classifying assessments in the system.
 */
public interface AssessmentClassificationService {

	/**
	 * Classify assessments for specified organizationId, iuv and iud
	 *
	 *@param assessmentsClassificationSemanticKeyDTO the DTO containing semantic keys such as organization ID, IUV, IUD.
	 * @return Assessments object with list of classified AssessmentDetails
	 */
	AssessmentEventDTO classifyAssessment(AssessmentsClassificationSemanticKeyDTO assessmentsClassificationSemanticKeyDTO);
}
