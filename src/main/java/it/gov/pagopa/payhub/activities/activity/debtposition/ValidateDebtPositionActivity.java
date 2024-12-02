package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionRequestDTO;

public interface ValidateDebtPositionActivity {

    DebtPositionDTO validate(DebtPositionRequestDTO debtPositionRequestDTO);
}
