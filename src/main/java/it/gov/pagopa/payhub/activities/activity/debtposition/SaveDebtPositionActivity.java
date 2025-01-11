package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;


/**
 * Service class responsible for saving debt positions.
 */
public interface SaveDebtPositionActivity {

    /**
     * Saves a new debt position.
     * <p>
     * This method processes the provided {@link DebtPositionDTO} and ensures it is persisted
     * using the underlying data storage mechanisms.
     * </p>
     *
     * @param debtPosition the {@link DebtPositionDTO} containing the details of the debt position to be saved
     */
    DebtPositionDTO saveDebtPosition(DebtPositionDTO debtPosition);
}
