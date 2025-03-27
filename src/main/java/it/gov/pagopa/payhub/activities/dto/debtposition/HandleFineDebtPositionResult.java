package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HandleFineDebtPositionResult {

    private DebtPositionDTO debtPositionDTO;
    private OffsetDateTime reductionEndDate;
    private boolean notified;
}
