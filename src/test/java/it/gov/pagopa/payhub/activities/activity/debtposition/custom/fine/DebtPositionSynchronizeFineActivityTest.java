package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.IONotificationMessage;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineValidation;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DebtPositionSynchronizeFineActivityTest {

    @Mock
    private DebtPositionFineValidation debtPositionFineValidationMock;

    private DebtPositionSynchronizeFineActivity activity;

    @BeforeEach
    void setUp(){
        activity = new DebtPositionSynchronizeFineActivityImpl(debtPositionFineValidationMock);
    }

    @Test
    void whenHandleFineDebtPositionThenOk(){
        //TODO to be fixed with task https://pagopa.atlassian.net/browse/P4ADEV-2442
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        FineWfExecutionConfig fineWfExecutionConfig = buildfineWfExecutionConfig();
        Mockito.when(debtPositionFineValidationMock.validateFine(debtPositionDTO))
                .thenReturn(true);

        DebtPositionDTO result = activity.handleFineDebtPosition(debtPositionDTO, false, fineWfExecutionConfig);

        assertEquals(debtPositionDTO, result);
    }

    private FineWfExecutionConfig buildfineWfExecutionConfig() {
        IONotificationMessage notified = new IONotificationMessage("notified", "notified");
        IONotificationMessage reductionExpired = new IONotificationMessage("reductionExpired", "reductionExpired");
        return FineWfExecutionConfig.builder()
                .ioMessages(new FineWfExecutionConfig.IONotificationFineWfMessages(notified, reductionExpired))
                .build();
    }
}
