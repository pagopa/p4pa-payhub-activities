package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;

import java.util.List;
import java.util.Optional;

/**
 * This interface provides methods for creating assessments in the system.
 */
public interface AssessmentService {
    /**
     * Creates assessments for the specified receipt ID.
     *
     * @param receiptId the unique identifier of the receipt for which assessments are to be created.
     * @return the created Assessments list.
     */
    List<Assessments> createAssessments(Long receiptId);

    /**
     * Finds assessments by organization ID and debt position type organization code.
     *
     * @param organizationId the unique identifier of the organization.
     * @param debtPositionTypeOrgCode the organization code for the debt position type.
     * @return an Optional containing the found Assessments, or empty if none found.
     */
    Optional<Assessments> findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(Long organizationId, String debtPositionTypeOrgCode, String assessmentName);


    /**
     * Creates a new assessment based on the provided AssessmentsRequestBody.
     *
     * @param assessmentsRequestBody the request body containing the details for the assessment to be created.
     * @return the created Assessments object.
     */
    Assessments createAssessment(AssessmentsRequestBody assessmentsRequestBody);
}
