package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.exception.ValidationException;

/**
 * Service class responsible to validate the new DebtPosition entity.
 * This class provides a method that validates mandatory fields or formality of field values about new debt position.
 */
public interface ValidateDebtPositionActivity {

    /**
     * Validates a new debt position values
     * @param debtPositionRequestDTO representing the new debt position to be inserted
     * @param debtPositionTypeOrgDTO representing the debt position type organization which the new debt position refers to
     * @return the {@link DebtPositionDTO} representing the debt position with the validated values
     * @throws ValidationException if a value does not comply with business rules
     */
    DebtPositionDTO validate(DebtPositionDTO debtPositionRequestDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO);
}
