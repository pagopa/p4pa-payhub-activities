package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.IONotificationService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = new IupdSyncStatusUpdateDTO(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID, "iupd");
        MessageResponseDTO expectedResult = new MessageResponseDTO("id");

        when(ioNotificationServiceMock.sendMessage(debtPosition, Map.of("IUD", iupdSyncStatusUpdateDTO)))
                .thenReturn(List.of(expectedResult));

        // When
        List<MessageResponseDTO> result = activity.sendMessage(debtPosition, Map.of("IUD", iupdSyncStatusUpdateDTO));

        // Then
        assertEquals(List.of(expectedResult), result);
    }
}
