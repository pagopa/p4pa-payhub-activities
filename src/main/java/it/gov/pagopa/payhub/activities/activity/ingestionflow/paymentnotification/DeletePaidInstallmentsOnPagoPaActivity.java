package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentnotification;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;

@ActivityInterface
public interface DeletePaidInstallmentsOnPagoPaActivity {

    /**
     * Deletes paid installments based on debt position and receipt id
     * @param debtPositionDTO the debt position
     * @param receiptId the receipt id
     */
    @ActivityMethod
    void deletePaidInstallmentsOnPagoPa(DebtPositionDTO debtPositionDTO, Long receiptId);

}
