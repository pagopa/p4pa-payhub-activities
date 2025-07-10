package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.debtposition.dto.generated.UpdateInstallmentNotificationDateRequest;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

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
    void givenAllPaymentsWithNotificationDateWhenRetrieveNotificationDateThenOk() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO dto = buildSendNotificationDTO();

        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId)).thenReturn(dto);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(sendNotificationId);

        // Then
        assertEquals(dto, result);
        Mockito.verify(sendServiceMock).retrieveNotificationDate(sendNotificationId);
        Mockito.verify(debtPositionServiceMock).updateInstallmentNotificationDate(Mockito.any());
    }

    @Test
    void givenAllPaymentsWithoutNotificationDateWhenRetrieveNotificationDateThenReturnNull() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO dto = buildSendNotificationDTO();
        dto.getPayments().getFirst().setNotificationDate(null);

        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId)).thenReturn(dto);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(sendNotificationId);

        // Then
        assertNull(result);
        Mockito.verifyNoInteractions(debtPositionServiceMock);
    }

    @Test
    void givenMixedPaymentsWithAndWithoutNotificationDateWhenRetrieveNotificationDateThenReturnNullAndPartialUpdate() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO dto = new SendNotificationDTO();

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
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(sendNotificationId);

        // Then
        assertNull(result);
        Mockito.verify(debtPositionServiceMock).updateInstallmentNotificationDate(updateRequest);
    }

    @Test
    void givenNoPaymentsWhenRetrieveNotificationDateThenReturnDTO() {
        // Given
        String sendNotificationId = "sendNotificationId";
        SendNotificationDTO dto = new SendNotificationDTO();
        dto.setPayments(Collections.emptyList());

        Mockito.when(sendServiceMock.retrieveNotificationDate(sendNotificationId)).thenReturn(dto);

        // When
        SendNotificationDTO result = sendNotificationDateRetrieve.sendNotificationDateRetrieve(sendNotificationId);

        // Then
        assertEquals(dto, result);
        Mockito.verifyNoInteractions(debtPositionServiceMock);
    }
}
