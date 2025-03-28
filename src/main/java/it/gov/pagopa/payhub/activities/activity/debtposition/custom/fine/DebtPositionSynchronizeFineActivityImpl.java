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
        log.info("Validate debt position fine with id: {}", debtPositionDTO.getDebtPositionId());
        debtPositionFineValidation.validateFine(debtPositionDTO);

        log.info("Processes notification date of debt position fine with id: {}", debtPositionDTO.getDebtPositionId());
        HandleFineDebtPositionResult handleFineDebtPositionResult = notificationDateProcessor.processNotificationDate(debtPositionDTO, executionParams);

        log.info("Handle reduction period of debt position fine with id: {}", debtPositionDTO.getDebtPositionId());
        debtPositionFineProcessor.processFine(handleFineDebtPositionResult);
        return handleFineDebtPositionResult;
    }
}
