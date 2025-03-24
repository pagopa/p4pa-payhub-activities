package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineValidation;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class DebtPositionSynchronizeFineActivityImpl implements DebtPositionSynchronizeFineActivity{

    private final DebtPositionFineValidation debtPositionFineValidation;

    public DebtPositionSynchronizeFineActivityImpl(DebtPositionFineValidation debtPositionFineValidation) {
        this.debtPositionFineValidation = debtPositionFineValidation;
    }

    @Override
    public DebtPositionDTO handleFineDebtPosition(DebtPositionDTO debtPositionDTO, boolean massive, FineWfExecutionConfig executionParams) {
        boolean fineValidated = debtPositionFineValidation.validateFine(debtPositionDTO);
        // TODO to be fully implemented with the task https://pagopa.atlassian.net/browse/P4ADEV-2442
        return debtPositionDTO;
    }
}
