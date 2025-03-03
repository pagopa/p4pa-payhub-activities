package it.gov.pagopa.payhub.activities.activity.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.time.LocalDate;

/**
 * Service interface responsible for handling the expiration process of a debt position.
 * This activity checks and updates the expiration status based on its installments.
 */
@ActivityInterface
public interface DebtPositionExpirationActivity {

    /**
     * Checks the expiration status of the installments linked to a specific debt position
     * and updates the overall status of the debt position accordingly.
     *
     * @param debtPositionId the unique identifier of the debt position to be processed.
     * @return the minimum due date ({@link LocalDate}) among all unpaid installments,
     *         or {@code null} if no unpaid installments exist.
     */
    @ActivityMethod
    LocalDate checkAndUpdateInstallmentExpiration(Long debtPositionId);
}
