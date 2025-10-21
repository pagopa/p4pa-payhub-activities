package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelAssessmentsDetail;

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

    /**
     * Find AssessmentsDetails by specified organizationId, iuv and iud.
     *
     * @param organizationId the unique identifier of the organization.
     * @param iuv the unique identifier of the transfer.
     * @param iud the unique identifier of the debt-position.
     * @return object containing a list of AssessmentsDetail found.
     */
    CollectionModelAssessmentsDetail findAssessmentsDetailByOrganizationIdAndIuvAndIud(Long organizationId, String iuv, String iud);

    /**
     * Update AssessmentsDetails based on the provided AssessmentsDetailRequestBody.
     *
     * @param assessmentDetailId the unique identifier of the assessment detail.
     * @param updateRequest the request body containing the details for the assessment detail to be updated.
     * @return the updated AssessmentsDetail object.
     */
    AssessmentsDetail updateAssessmentsDetail(Long assessmentDetailId, AssessmentsDetailRequestBody updateRequest);
}
