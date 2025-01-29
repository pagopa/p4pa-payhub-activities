package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * Service class responsible for invoking the ACA service to handle an installment of debt position
 */
@ActivityInterface
public interface SynchronizeInstallmentAcaActivity {

    /**
     * Handles an installment of debt position in ACA Service.
     * This method invokes the ACA service to handle an installment of debt position
     *
     * @param debtPositionDTO the {@link DebtPositionDTO} containing the details of the debt position
     * @param iud the IUD of installment to be handled
     */
    @ActivityMethod
    void synchronizeInstallmentAca(DebtPositionDTO debtPositionDTO, String iud);

}
