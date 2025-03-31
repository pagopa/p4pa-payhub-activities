package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.debtposition.dto.generated.UpdateInstallmentNotificationDateRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.SendNotificationFaker.buildSendNotificationDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SendNotificationDateRetrieveActivityTest {

    @Mock
    private SendService sendServiceMock;
    @Mock
    private DebtPositionService debtPositionServiceMock;

    private SendNotificationDateRetrieveActivityImpl sendNotificationDateRetrieve;

    @BeforeEach
    void init() {
        sendNotificationDateRetrieve = new SendNotificationDateRetrieveActivityImpl(sendServiceMock, debtPositionServiceMock);
    }

    @Test
    void givenSendNotificationIdWhenRetrieveNotificationDateThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO sendNotification = buildSendNotificationDTO();
        UpdateInstallmentNotificationDateRequest updateRequest = UpdateInstallmentNotificationDateRequest.builder()
                .debtPositionId(123L)
                .nav(Collections.singletonList("nav"))
                .notificationDate(OFFSETDATETIME)
                .build();

        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId))
                .thenReturn(sendNotification);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(sendNotificationId);

        // Then
        assertEquals(sendNotification, result);
        Mockito.verify(debtPositionServiceMock).updateInstallmentNotificationDate(updateRequest);
    }

    @Test
    void givenSendNotificationNullWhenRetrieveNotificationDateThenNull() {
        // Given
        String sendNotificationId = "sendNotificationId";

        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId))
                .thenReturn(null);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(sendNotificationId);

        // Then
        assertNull(result);
    }

    @Test
    void givenSendNotificationDateNullWhenRetrieveNotificationDateThenNull() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO sendNotification = buildSendNotificationDTO();
        sendNotification.setNotificationDate(null);

        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId))
                .thenReturn(sendNotification);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(sendNotificationId);

        // Then
        assertNull(result);
    }

}