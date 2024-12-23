package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class SendDebtPositionIONotificationActivityTest {
    @Mock
    private IONotificationService ioNotificationServiceMock;

    private SendDebtPositionIONotificationActivity activity;

    @BeforeEach
    void init() {
        activity = new SendDebtPositionIONotificationActivityImpl(ioNotificationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(ioNotificationServiceMock);
    }

    @Test
    void givenSendMessageThenSuccess(){
        // Given
        DebtPositionDTO debtPosition = buildDebtPositionDTO();

        // When
        activity.sendMessage(debtPosition);

        // Then
        Mockito.verify(ioNotificationServiceMock, Mockito.times(1))
                .sendMessage(debtPosition);
    }
}
