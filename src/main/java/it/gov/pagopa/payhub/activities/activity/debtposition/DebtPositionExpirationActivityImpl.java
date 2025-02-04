package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Lazy
@Service
public class DebtPositionExpirationActivityImpl implements DebtPositionExpirationActivity{

    private final DebtPositionService debtPositionService;

    public DebtPositionExpirationActivityImpl(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    @Override
    public OffsetDateTime checkAndUpdateInstallmentExpiration(Long debtPositionId) {
        return debtPositionService.checkAndUpdateInstallmentExpiration(debtPositionId);
    }
}
