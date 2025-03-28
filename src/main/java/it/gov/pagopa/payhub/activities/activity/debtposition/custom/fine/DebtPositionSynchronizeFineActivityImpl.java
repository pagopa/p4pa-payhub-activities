package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineNotificationDateProcessor;
import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineProcessor;
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
    private final DebtPositionFineNotificationDateProcessor notificationDateProcessor;
    private final DebtPositionFineProcessor debtPositionFineProcessor;

    public DebtPositionSynchronizeFineActivityImpl(DebtPositionFineValidation debtPositionFineValidation, DebtPositionFineNotificationDateProcessor notificationDateProcessor, DebtPositionFineProcessor debtPositionFineProcessor) {
        this.debtPositionFineValidation = debtPositionFineValidation;
        this.notificationDateProcessor = notificationDateProcessor;
        this.debtPositionFineProcessor = debtPositionFineProcessor;
    }

    @Override
    public HandleFineDebtPositionResult handleFineDebtPosition(DebtPositionDTO debtPositionDTO, boolean massive, FineWfExecutionConfig executionParams) {
        debtPositionFineValidation.validateFine(debtPositionDTO);
        HandleFineDebtPositionResult handleFineDebtPositionResult = notificationDateProcessor.processNotificationDate(debtPositionDTO, executionParams);
        debtPositionFineProcessor.processFine(handleFineDebtPositionResult);
        // TODO to be fully implemented with the task https://pagopa.atlassian.net/browse/P4ADEV-2442
        return handleFineDebtPositionResult;
    }
}
