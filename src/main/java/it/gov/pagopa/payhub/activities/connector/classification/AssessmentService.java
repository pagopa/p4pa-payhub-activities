package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import java.util.List;

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
}
