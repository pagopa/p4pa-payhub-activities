package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendNotificationNotFoundException;
import it.gov.pagopa.pu.debtposition.dto.generated.UpdateInstallmentNotificationDateRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static it.gov.pagopa.payhub.activities.util.faker.SendNotificationFaker.buildSendNotificationDTO;
import static org.junit.jupiter.api.Assertions.*;

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
    void givenAllPaymentsWithNotificationDateWhenRetrieveNotificationDateThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";
        String notificationRequestId = "notificationRequestId";
        SendNotificationDTO dto = buildSendNotificationDTO(sendNotificationId);

        Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(dto);
        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId)).thenReturn(dto);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(notificationRequestId);

        // Then
        assertEquals(dto, result);
        Mockito.verify(sendServiceMock).retrieveNotificationDate(sendNotificationId);
        Mockito.verify(debtPositionServiceMock).updateInstallmentNotificationDate(Mockito.any());
    }

    @Test
    void givenAllPaymentsWithoutNotificationDateWhenRetrieveNotificationDateThenReturnNull() {
        // Given
        String notificationRequestId = "notificationRequestId";
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO dto = buildSendNotificationDTO(sendNotificationId);
        dto.getPayments().getFirst().setNotificationDate(null);

        Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(dto);
        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId)).thenReturn(dto);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(notificationRequestId);

        // Then
        assertNull(result);
        Mockito.verifyNoInteractions(debtPositionServiceMock);
    }

    @Test
    void givenMixedPaymentsWithAndWithoutNotificationDateWhenRetrieveNotificationDateThenReturnNullAndPartialUpdate() {
        // Given
        String notificationRequestId = "notificationRequestId";
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO dto = new SendNotificationDTO();
        dto.sendNotificationId(sendNotificationId);

        Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(dto);

        SendNotificationPaymentsDTO paymentWithDate = new SendNotificationPaymentsDTO();
        paymentWithDate.setDebtPositionId(123L);
        paymentWithDate.setNavList(List.of("nav1"));
        paymentWithDate.setNotificationDate(OFFSETDATETIME);

        SendNotificationPaymentsDTO paymentWithoutDate = new SendNotificationPaymentsDTO();
        paymentWithoutDate.setDebtPositionId(456L);
        paymentWithoutDate.setNavList(List.of("nav2"));
        paymentWithoutDate.setNotificationDate(null);

        UpdateInstallmentNotificationDateRequest updateRequest = UpdateInstallmentNotificationDateRequest.builder()
                .debtPositionId(123L)
                .nav(Collections.singletonList("nav1"))
                .notificationDate(OFFSETDATETIME)
                .build();

        dto.setPayments(List.of(paymentWithDate, paymentWithoutDate));

        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId)).thenReturn(dto);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(notificationRequestId);

        // Then
        assertNull(result);
        Mockito.verify(debtPositionServiceMock).updateInstallmentNotificationDate(updateRequest);
    }

    @Test
    void givenNoPaymentsWhenRetrieveNotificationDateThenReturnDTO() {
        // Given
        String notificationRequestId = "notificationRequestId";
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO dto = new SendNotificationDTO();
        dto.setSendNotificationId(sendNotificationId);
        dto.setPayments(Collections.emptyList());

        Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(dto);
        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId)).thenReturn(dto);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(notificationRequestId);

        // Then
        assertEquals(dto, result);
        Mockito.verifyNoInteractions(debtPositionServiceMock);
    }

    @Test
    void givenNoNotificationWhenRetrieveNotificationDateThenReturnNull() {
        // Given
        String notificationRequestId = "notificationRequestId";

        Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId))
                .thenReturn(null);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(notificationRequestId);

        // Then
        assertNull(result);
        Mockito.verify(sendServiceMock, Mockito.times(0)).retrieveNotificationDate(notificationRequestId);
        Mockito.verifyNoInteractions(debtPositionServiceMock);
    }

    @Test
    void givenNotFoundNotificationWhenRetrieveNotificationDateThenThrowSendNotificationNotFoundException() {
        // Given
        String notificationRequestId = "notificationRequestId";

        Mockito.doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null))
                .when(sendServiceMock)
                .retrieveNotificationByNotificationRequestId(notificationRequestId);

        // When
        SendNotificationNotFoundException notRetryableActivityException = Assertions.assertThrows(
                SendNotificationNotFoundException.class,
                () -> sendNotificationDateRetrieve.sendNotificationDateRetrieve(notificationRequestId)
        );

        // Then
        assertNotNull(notRetryableActivityException);
        assertEquals(
            "Notification for notificationRequestId %s not found: error message 404 NotFound".formatted(notificationRequestId),
            notRetryableActivityException.getMessage()
        );
        Mockito.verify(sendServiceMock, Mockito.times(0)).retrieveNotificationDate(notificationRequestId);
        Mockito.verifyNoInteractions(debtPositionServiceMock);
    }
}
