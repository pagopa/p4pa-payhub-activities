package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.DuplicatePaymentReportingCheckActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.DuplicatePaymentsReportingQueryDTO;

/**
 * Interface for handling duplicate Payments Reporting.
 */
@ActivityInterface
public interface DuplicatePaymentReportingCheckActivity {

  /**
   * Checks for duplicate Payments Reporting and creates a Classification with label {@link it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum#DOPPI} if multiple payments are found.<br>
   * For multiple Payments Reporting to be duplicates they need to have:
   * <ul>
   *   <li>same IUV</li>
   *   <li>same amount</li>
   *   <li>same organization (identified by orgId)</li>
   *   <li>same transferIndex</li>
   *   <li>different IUR</li>
   * </ul>
   *
   * @param queryFields the DTO containing information to delete a specific classification and then find possible duplicate Payments Reporting
   * @return
   */
  @ActivityMethod
  DuplicatePaymentReportingCheckActivityResult duplicateCheck(DuplicatePaymentsReportingQueryDTO queryFields);
}
