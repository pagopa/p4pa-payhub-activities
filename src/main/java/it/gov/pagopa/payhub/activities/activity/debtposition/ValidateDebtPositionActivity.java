package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;

/**
 * Service class responsible to validate the new DebtPosition entity.
 * This class provides a method that validates mandatory fields or formality of field values about new debt position.
 */
public interface ValidateDebtPositionActivity {

    /**
     * Validates a new debt position values
     * @param debtPositionRequestDTO representing the new debt position to be validated
     * @throws InvalidValueException if a value does not comply with business rules
     */
    void validate(DebtPositionDTO debtPositionRequestDTO);
}
