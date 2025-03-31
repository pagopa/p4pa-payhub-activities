package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineReductionOptionExpirationProcessor;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class DebtPositionFineReductionOptionExpirationActivityImpl implements DebtPositionFineReductionOptionExpirationActivity {

    private final DebtPositionFineReductionOptionExpirationProcessor fineReductionOptionExpirationProcessor;

    public DebtPositionFineReductionOptionExpirationActivityImpl(DebtPositionFineReductionOptionExpirationProcessor fineReductionOptionExpirationProcessor) {
        this.fineReductionOptionExpirationProcessor = fineReductionOptionExpirationProcessor;
    }

    @Override
    public DebtPositionDTO handleFineReductionExpiration(Long debtPositionId) {
        log.info("Handling fine reduction expiration on debtPositionId: {}", debtPositionId);
        return fineReductionOptionExpirationProcessor.handleFineReductionExpiration(debtPositionId);
    }
}
