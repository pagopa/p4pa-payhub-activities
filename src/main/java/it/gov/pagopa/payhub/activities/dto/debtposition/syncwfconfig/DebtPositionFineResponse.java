package it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig;

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
public class DebtPositionFineResponse {

    private DebtPositionDTO debtPositionDTO;
    private OffsetDateTime reductionEndDate;
    private boolean notified;
}
