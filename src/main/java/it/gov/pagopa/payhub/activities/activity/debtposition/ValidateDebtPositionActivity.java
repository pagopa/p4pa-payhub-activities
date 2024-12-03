package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;

public interface ValidateDebtPositionActivity {

    DebtPositionDTO validate(DebtPositionDTO debtPositionRequestDTO, DebtPositionTypeOrgDTO debtPositionTypeOrgDTO);
}
