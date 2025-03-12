package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class SendDebtPositionIONotificationActivityTest {
    @Mock
    private IONotificationFacadeService ioNotificationFacadeServiceMock;

    private SendDebtPositionIONotificationActivity activity;

    @BeforeEach
    void init() {
        activity = new SendDebtPositionIONotificationActivityImpl(ioNotificationFacadeServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(ioNotificationFacadeServiceMock);
    }

    @Test
    void givenSendMessageThenSuccess(){
        // Given
        DebtPositionDTO debtPosition = buildDebtPositionDTO();

        // When
        activity.sendMessage(debtPosition, null);

        // Then
        Mockito.verify(ioNotificationFacadeServiceMock, Mockito.times(1))
                .sendMessage(new NotificationRequestDTO());
    }
}
