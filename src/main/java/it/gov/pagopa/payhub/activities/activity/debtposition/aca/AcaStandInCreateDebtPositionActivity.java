package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * Service class responsible for invoking the ACA service to create a debt position
 */
@ActivityInterface
public interface AcaStandInCreateDebtPositionActivity {

    /**
     * Creates a new debt position in ACA Service.
     * This method invokes the ACA service to create a new debt position
     *
     * @param debtPositionDTO the {@link DebtPositionDTO} containing the details of the debt position to be created
     */
    @ActivityMethod
    void createAcaDebtPosition(DebtPositionDTO debtPositionDTO);

}
