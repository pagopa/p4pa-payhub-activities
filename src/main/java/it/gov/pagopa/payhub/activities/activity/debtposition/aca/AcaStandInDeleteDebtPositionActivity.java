package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * Service class responsible for invoking the ACA service to delete a debt position
 */
@ActivityInterface
public interface AcaStandInDeleteDebtPositionActivity {

    /**
     * Deletes a debt position in ACA Service.
     * This method invokes the ACA service to delete a debt position
     *
     * @param debtPositionDTO the {@link DebtPositionDTO} containing the details of the debt position to be deleted
     */
    @ActivityMethod
    void deleteAcaDebtPosition(DebtPositionDTO debtPositionDTO);

}
