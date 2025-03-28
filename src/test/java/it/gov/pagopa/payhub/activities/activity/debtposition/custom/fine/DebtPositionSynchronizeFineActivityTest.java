package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.dto.IONotificationMessage;
import it.gov.pagopa.payhub.activities.dto.debtposition.HandleFineDebtPositionResult;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.FineWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineNotificationDateProcessor;
import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineProcessor;
import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineValidation;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DebtPositionSynchronizeFineActivityTest {

    @Mock
    private DebtPositionFineValidation debtPositionFineValidationMock;
    @Mock
    private DebtPositionFineNotificationDateProcessor notificationDateProcessorMock;
    @Mock
    private DebtPositionFineProcessor debtPositionFineProcessorMock;

    private DebtPositionSynchronizeFineActivity activity;

    @BeforeEach
    void setUp(){
        activity = new DebtPositionSynchronizeFineActivityImpl(debtPositionFineValidationMock, notificationDateProcessorMock, debtPositionFineProcessorMock);
    }

    @Test
    void whenHandleFineDebtPositionThenOk(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
        FineWfExecutionConfig fineWfExecutionConfig = buildfineWfExecutionConfig();

        HandleFineDebtPositionResult expectedResult = new HandleFineDebtPositionResult(debtPositionDTO, OffsetDateTime.now().plusDays(2), true);
        Mockito.when(notificationDateProcessorMock.processNotificationDate(debtPositionDTO, fineWfExecutionConfig))
                .thenReturn(expectedResult);

        // When
        HandleFineDebtPositionResult result = activity.handleFineDebtPosition(debtPositionDTO, false, fineWfExecutionConfig);

        // Then
        verify(debtPositionFineValidationMock).validateFine(debtPositionDTO);
        verify(debtPositionFineProcessorMock).processFine(expectedResult);
        assertEquals(expectedResult, result);
    }

    private FineWfExecutionConfig buildfineWfExecutionConfig() {
        IONotificationMessage notified = new IONotificationMessage("notified", "notified");
        IONotificationMessage reductionExpired = new IONotificationMessage("reductionExpired", "reductionExpired");
        return FineWfExecutionConfig.builder()
                .ioMessages(new FineWfExecutionConfig.IONotificationFineWfMessages(notified, reductionExpired))
                .build();
    }
}
