package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;

/**
 * This interface provides methods for creating assessments detail in the system.
 */
public interface AssessmentsDetailService {

    /**
     * Creates a new assessment based on the provided AssessmentsRequestBody.
     *
     * @param assessmentsDetailRequestBody the request body containing the details for the assessment detail to be created.
     * @return the created AssessmentsDetail object.
     */
    AssessmentsDetail createAssessmentDetail(AssessmentsDetailRequestBody assessmentsDetailRequestBody);
}
