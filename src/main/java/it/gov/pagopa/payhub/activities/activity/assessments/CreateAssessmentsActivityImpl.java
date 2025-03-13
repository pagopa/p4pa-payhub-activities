package it.gov.pagopa.payhub.activities.activity.assessments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.service.assessments.AssessmentsService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Activity for creating assessments.
 * This activity is responsible for handling the creation of assessments based on a given receipt ID.
 */
@ActivityInterface
@Slf4j
public class CreateAssessmentsActivityImpl implements CreateAssessmentsActivity {
  private final AssessmentsService assessmentsService;

  public CreateAssessmentsActivityImpl(AssessmentsService assessmentsService) {
    this.assessmentsService = assessmentsService;
  }


  /**
   * Creates assessments for the specified receipt ID.
   *
   * @param receiptId the unique identifier of the receipt for which assessments are to be created.
   */
  @ActivityMethod
  public void createAssessments(Long receiptId) {

    List<InstallmentNoPIIResponse> installmentNoPIIList = assessmentsService.getInstallmentsByReceiptId(receiptId);

    log.info("Installments retrieved: {}", installmentNoPIIList.size());
  }


}
