package it.gov.pagopa.payhub.activities.activity.assessments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.classification.dto.generated.DebtPositionDTO;
import java.util.List;

/**
 * Interface for creating assessments registry in the system.
 * This activity is responsible for handling the creation of assessments
 * registry based on a given debtPositionDTO and a list of IUD.
 */
@ActivityInterface
public interface AssessmentsRegistryCreationActivity {

  /**
   * Creates assessments registry if not already exists.
   *
   * @param debtPositionDTO debt position to create assessment registry
   * @param iudList list of IUD
   */
  @ActivityMethod
  void createAssessmentsRegistryByDebtPositionDTOAndIudList(DebtPositionDTO debtPositionDTO, List<String> iudList);
}
