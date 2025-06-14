package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import java.util.List;

/**
 * This interface provides methods for creating assessments registry in the system.
 */
public interface AssessmentsRegistryService {

  /**
   * Creates assessments registry by specified debtPositionDTO and IUDList if not already exists.
   *
   * @param debtPositionDTO debt position to create assessment registry
   * @param iudList list of IUD
   */
  void createAssessmentsRegistryByDebtPositionDTOAndIudList(DebtPositionDTO debtPositionDTO, List<String> iudList);
}
