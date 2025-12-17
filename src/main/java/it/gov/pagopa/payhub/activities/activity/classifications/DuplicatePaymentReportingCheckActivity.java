package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.DuplicatePaymentsReportingCheckDTO;

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
   *   <li>same organizationId</li>
   *   <li>same transferIndex</li>
   *   <li>same orgFiscalCode on Receipt</li>
   *   <li>different IUR</li>
   * </ul>
   *
   * @param duplicatePaymentsReportingCheckDTO DTO containing information to query for duplicates Classifications and Payments Reportings
   * @param transferIur Identifies the receipt to get missing information like receiptPaymentAmount and orgFiscalCode
   */
  @ActivityMethod
  void duplicateCheck(DuplicatePaymentsReportingCheckDTO duplicatePaymentsReportingCheckDTO, String transferIur);
}
