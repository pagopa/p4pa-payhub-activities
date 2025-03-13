package it.gov.pagopa.payhub.activities.activity.assessments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Activity for creating assessments.
 * This activity is responsible for handling the creation of assessments based on a given receipt ID.
 */
@ActivityInterface
public interface CreateAssessmentsActivity {

  /**
   * Creates assessments for the specified receipt ID.
   *
   * @param receiptId the unique identifier of the receipt for which assessments are to be created.
   */
  @ActivityMethod
  void createAssessments(Long receiptId);

}
