package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;

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
   * @param organizationId The ID of the organization
   * @param transfer2ClassifyDTO Identifies the transfer to be classified
   */
  @ActivityMethod
  void duplicatePaymentsCheck(Long organizationId, Transfer2ClassifyDTO transfer2ClassifyDTO);
}
