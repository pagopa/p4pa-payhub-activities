package it.gov.pagopa.payhub.activities.activity.debtposition.gpd;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

/**
 * Service class responsible for invoking the GPD service to synchronize an installment of debt position
 */
@ActivityInterface
public interface SynchronizeInstallmentGpdActivity {

    /**
     * Synchronize an installment of debt position in GPD Service.
     * This method invokes the GPD service to synchronize an installment of debt position
     *
     * @param debtPositionDTO the {@link DebtPositionDTO} containing the details of the debt position
     * @param iud the IUD of installment to be synchronized
     * @return the iupdPagopa synchronized
     */
    @ActivityMethod
    String synchronizeInstallmentGpd(DebtPositionDTO debtPositionDTO, String iud);

}
