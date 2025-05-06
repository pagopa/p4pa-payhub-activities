package it.gov.pagopa.payhub.activities.activity.debtposition.synchronize.gpdpreload;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * Service class responsible for invoking the GPD PreLoad service to synchronize an installment of debt position
 */
@ActivityInterface
public interface SynchronizeInstallmentGpdPreLoadActivity {

    /**
     * Synchronize an installment of debt position in GPD PreLoad Service.
     * This method invokes the GPD PreLoad service to synchronize an installment of debt position
     *
     * @param debtPositionDTO the {@link DebtPositionDTO} containing the details of the debt position
     * @param iud the IUD of installment to be synchronized
     */
    @ActivityMethod
    void synchronizeInstallmentGpdPreLoad(DebtPositionDTO debtPositionDTO, String iud);

}
