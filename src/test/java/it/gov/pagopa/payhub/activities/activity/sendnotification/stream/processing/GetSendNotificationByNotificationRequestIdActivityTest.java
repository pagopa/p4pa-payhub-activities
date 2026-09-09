package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendStreamSkippedEventException;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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
    void whenGetSendNotificationByNotificationRequestIdThenOk() {
        // Given
        String notificationId = "sendNotificationId";
        String notificationRequestId = "notificationRequestId";
        SendNotificationDTO expectedResponse = new SendNotificationDTO();
        expectedResponse.setSendNotificationId(notificationId);

        when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(expectedResponse);

        // When
        SendNotificationDTO result = activity.getSendNotificationByNotificationRequestId(notificationRequestId);

        // Then
        assertSame(expectedResponse, result);
    }

    @Test
    void givenNotFoundExceptionWhenGetSendNotificationByNotificationRequestIdThenReturnNull() {
        // Given
        String notificationRequestId = "notificationRequestId";

        doThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"))
                .when(sendServiceMock)
                .retrieveNotificationByNotificationRequestId(notificationRequestId);

        // When
        SendNotificationDTO result = activity.getSendNotificationByNotificationRequestId(notificationRequestId);

        // Then
        assertNull(result);
    }

}