package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetSendNotificationActivityTest {

    @Mock
    private SendNotificationService serviceMock;

    private GetSendNotificationActivity activity;

    @BeforeEach
    void init(){
        this.activity = new GetSendNotificationActivityImpl(serviceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void whenGetThenInvokeService(){
        // Given
        String sendNotificationId = "ID";
        SendNotificationDTO expectedResult = new SendNotificationDTO();

        Mockito.when(serviceMock.getSendNotification(sendNotificationId))
                .thenReturn(expectedResult);

        // When
        SendNotificationDTO result = activity.getSendNotification(sendNotificationId);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
