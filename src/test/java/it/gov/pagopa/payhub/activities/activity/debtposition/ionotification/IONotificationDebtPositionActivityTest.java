package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIoNotificationDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.GenericWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.IONotificationService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncCompleteDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IONotificationDebtPositionActivityTest {
    @Mock
    private IONotificationService ioNotificationServiceMock;

    private IONotificationDebtPositionActivity activity;

    @BeforeEach
    void init() {
        activity = new IONotificationDebtPositionActivityImpl(ioNotificationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(ioNotificationServiceMock);
    }

    @Test
    void givenSendIoNotificationThenSuccess(){
        // Given
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        SyncCompleteDTO syncStatusCompleteDTO = new SyncCompleteDTO(InstallmentStatus.UNPAID);
        DebtPositionIoNotificationDTO expectedResult = new DebtPositionIoNotificationDTO();
        GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages = new GenericWfExecutionConfig.IONotificationBaseOpsMessages();
        Map<String, SyncCompleteDTO> iudMap = Map.of("IUD", syncStatusCompleteDTO);

        when(ioNotificationServiceMock.sendMessage(Mockito.same(debtPosition), Mockito.same(iudMap), Mockito.same(ioMessages)))
                .thenReturn(expectedResult);

        // When
        DebtPositionIoNotificationDTO result = activity.sendIoNotification(debtPosition, iudMap, ioMessages);

        // Then
        assertSame(expectedResult, result);
    }
}
