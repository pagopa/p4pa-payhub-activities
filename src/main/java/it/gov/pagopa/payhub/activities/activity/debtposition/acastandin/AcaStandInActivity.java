package it.gov.pagopa.payhub.activities.activity.debtposition.acastandin;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * Service class responsible for invoking the ACA service to create, update or delete a debt position
 */
public interface AcaStandInActivity {

    /**
     * Creates a new debt position in ACA Service.
     * This method invokes the ACA service to create a new debt position
     *
     * @param debtPositionDTO the {@link DebtPositionDTO} containing the details of the debt position to be created
     */
    void createAcaDebtPosition(DebtPositionDTO debtPositionDTO);

}
