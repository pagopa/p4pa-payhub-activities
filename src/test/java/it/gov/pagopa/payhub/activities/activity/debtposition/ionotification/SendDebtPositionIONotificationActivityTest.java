package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.mapper.NotificationQueueMapper;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.SendDebtPositionIONotificationService;
import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.utility.faker.NotificationQueueFaker.buildNotificationQueueDTO;

@ExtendWith(MockitoExtension.class)
class SendDebtPositionIONotificationActivityTest {

    @Mock
    private NotificationQueueMapper notificationQueueMapperMock;
    @Mock
    private SendDebtPositionIONotificationService sendDebtPositionIONotificationServiceMock;

    private SendDebtPositionIONotificationActivity activity;

    @BeforeEach
    void init() {
        activity = new SendDebtPositionIONotificationActivityImpl(sendDebtPositionIONotificationServiceMock, notificationQueueMapperMock);
    }

    @Test
    void givenSendMessageThenSuccess(){
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        NotificationQueueDTO notificationQueueDTO = buildNotificationQueueDTO();

        Mockito.when(notificationQueueMapperMock.mapDebtPositionDTO2NotificationQueueDTO(debtPosition))
                .thenReturn(List.of(notificationQueueDTO));

        Mockito.doNothing().when(sendDebtPositionIONotificationServiceMock).sendMessage(notificationQueueDTO);

        activity.sendMessage(debtPosition);

        Mockito.verify(notificationQueueMapperMock, Mockito.times(1))
                .mapDebtPositionDTO2NotificationQueueDTO(debtPosition);

        Mockito.verify(sendDebtPositionIONotificationServiceMock, Mockito.times(1))
                .sendMessage(notificationQueueDTO);

        Mockito.verifyNoMoreInteractions(notificationQueueMapperMock, sendDebtPositionIONotificationServiceMock);
    }
}
