package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationClientService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
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
    private IONotificationClientService ioNotificationClientServiceMock;

    private SendDebtPositionIONotificationActivity activity;

    @BeforeEach
    void init() {
        activity = new SendDebtPositionIONotificationActivityImpl(ioNotificationClientServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(ioNotificationClientServiceMock);
    }

    @Test
    void givenSendMessageThenSuccess(){
        // Given
        DebtPositionDTO debtPosition = buildDebtPositionDTO();

        // When
        activity.sendMessage(debtPosition, null);

        // Then
        Mockito.verify(ioNotificationClientServiceMock, Mockito.times(1))
                .sendMessage(debtPosition);
    }
}
