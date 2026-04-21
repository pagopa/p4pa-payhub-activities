package it.gov.pagopa.payhub.activities.activity.sendnotification.delete;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.FileExpirationResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

@ExtendWith(MockitoExtension.class)
class DeleteSendLegalFactFileActivityTest {

    @Mock
    private SendNotificationService sendNotificationServiceMock;

    private DeleteSendLegalFactFileActivityImpl deleteSendLegalFactFileActivity;

    @BeforeEach
    void init() {
        deleteSendLegalFactFileActivity = new DeleteSendLegalFactFileActivityImpl(sendNotificationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(sendNotificationServiceMock);
    }

    @Test
    void whenDeleteSendLegalFactFileThenOk() {
        String sendNotificationId = "sendNotificationId";
        FileExpirationResponseDTO fileExpirationResponseDTO = new FileExpirationResponseDTO(OffsetDateTime.now());
        
        Mockito.when(sendNotificationServiceMock.deleteExpiredLegalFacts(sendNotificationId)).thenReturn(fileExpirationResponseDTO);

        OffsetDateTime response = deleteSendLegalFactFileActivity.deleteSendLegalFactFile(sendNotificationId);

        Assertions.assertEquals(fileExpirationResponseDTO.getNextFileExpirationDate(),response);
    }

    @Test
    void givenNullResponseWhenDeleteSendLegalFactFileThenNull() {
        String sendNotificationId = "sendNotificationId";

        Mockito.when(sendNotificationServiceMock.deleteExpiredLegalFacts(sendNotificationId)).thenReturn(null);

        OffsetDateTime response = deleteSendLegalFactFileActivity.deleteSendLegalFactFile(sendNotificationId);

        Assertions.assertNull(response);
    }
}