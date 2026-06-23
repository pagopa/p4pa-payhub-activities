package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendStreamSkippedEventException;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetSendNotificationByNotificationRequestIdActivityTest {

    @Mock
    private SendService sendServiceMock;

    private GetSendNotificationByNotificationRequestIdActivity activity;

    @BeforeEach
    void init() {
        activity = new GetSendNotificationByNotificationRequestIdActivityImpl(
                sendServiceMock
        );
    }

    @Test
    void whenSendNotificationStatusThenOk() {
        // Given
        String notificationId = "sendNotificationId";
        String notificationRequestId = "notificationRequestId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();
        expectedResponse.setSendNotificationId(notificationId);

        Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = activity.getSendNotificationByNotificationRequestId(notificationRequestId);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void givenNotFoundExceptionWhenSendNotificationStatusThenThrowNotRetryableActivityException() {
        // Given
        String notificationId = "sendNotificationId";
        String notificationRequestId = "notificationRequestId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();
        expectedResponse.setSendNotificationId(notificationId);

        Mockito.doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null))
                .when(sendServiceMock)
                .retrieveNotificationByNotificationRequestId(notificationRequestId);

        // When
        SendStreamSkippedEventException sendStreamSkippedEventException = Assertions.assertThrows(
                SendStreamSkippedEventException.class,
                () -> activity.getSendNotificationByNotificationRequestId(notificationRequestId)
        );

        // Then
        Assertions.assertNotNull(sendStreamSkippedEventException);
        String causeErrorMessage = "Notification for notificationRequestId %s not found: error message 404 NotFound".formatted(notificationRequestId);
        Assertions.assertEquals(
                "Skipped an error during execution of activity %s: %s".formatted(ValidateSendNotificationStatusActivity.class.getSimpleName(), causeErrorMessage),
                sendStreamSkippedEventException.getMessage()
        );
    }

}