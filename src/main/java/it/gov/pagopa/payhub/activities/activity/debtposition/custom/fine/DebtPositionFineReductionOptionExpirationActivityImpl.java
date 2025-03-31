package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class DebtPositionFineReductionOptionExpirationActivityImpl implements DebtPositionFineReductionOptionExpirationActivity{

    @Override
    public DebtPositionDTO handleFineReductionExpiration(Long debtPositionId) {
        return null;
    }
}
