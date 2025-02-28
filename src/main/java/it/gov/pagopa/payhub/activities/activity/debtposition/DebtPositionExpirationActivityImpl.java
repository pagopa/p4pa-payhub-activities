package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Lazy
@Service
public class DebtPositionExpirationActivityImpl implements DebtPositionExpirationActivity{

    private final DebtPositionService debtPositionService;

    public DebtPositionExpirationActivityImpl(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    @Override
    public LocalDate checkAndUpdateInstallmentExpiration(Long debtPositionId) {
        log.info("Checking expiration of DebtPosition {}", debtPositionId);
        return debtPositionService.checkAndUpdateInstallmentExpiration(debtPositionId);
    }
}
