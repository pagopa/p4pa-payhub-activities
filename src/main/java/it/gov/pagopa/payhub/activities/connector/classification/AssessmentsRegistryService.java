package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsRegistrySemanticKey;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

import java.util.List;
import java.util.Optional;

/**
 * This interface provides methods for creating assessments registry in the system.
 */
public interface AssessmentsRegistryService {

    /**
     * Creates assessments registry by specified debtPositionDTO and IUDList if not already exists.
     *
     * @param debtPositionDTO debt position to create assessment registry
     * @param iudList         list of IUD
     */
    void createAssessmentsRegistryByDebtPositionDTOAndIudList(DebtPositionDTO debtPositionDTO, List<String> iudList);

    void createAssessmentsRegistry(AssessmentsRegistry assessmentsRegistry);

    Optional<AssessmentsRegistry> searchAssessmentsRegistryBySemanticKey(AssessmentsRegistrySemanticKey semanticKey);
}
