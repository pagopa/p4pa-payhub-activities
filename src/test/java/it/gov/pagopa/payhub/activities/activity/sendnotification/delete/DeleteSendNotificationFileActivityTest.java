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
class DeleteSendNotificationFileActivityTest {

    @Mock
    private SendNotificationService sendNotificationServiceMock;

    private DeleteSendNotificationFileActivity deleteSendNotificationFileActivity;

    @BeforeEach
    void init() {
        deleteSendNotificationFileActivity = new DeleteSendNotificationFileActivityImpl(sendNotificationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(sendNotificationServiceMock);
    }

    @Test
    void whenDeleteSendNotificationExpiredFilesThenOk() {
        String sendNotificationId = "sendNotificationId";
        FileExpirationResponseDTO fileExpirationResponseDTO = new FileExpirationResponseDTO(OffsetDateTime.now());
        
        Mockito.when(sendNotificationServiceMock.deleteExpiredDocuments(sendNotificationId)).thenReturn(fileExpirationResponseDTO);

        OffsetDateTime response = deleteSendNotificationFileActivity.deleteSendNotificationExpiredFiles(sendNotificationId);

        Assertions.assertEquals(fileExpirationResponseDTO.getNextFileExpirationDate(),response);
    }

    @Test
    void givenNullResponseWhenDeleteSendNotificationExpiredFilesThenNull() {
        String sendNotificationId = "sendNotificationId";

        Mockito.when(sendNotificationServiceMock.deleteExpiredDocuments(sendNotificationId)).thenReturn(null);

        OffsetDateTime response = deleteSendNotificationFileActivity.deleteSendNotificationExpiredFiles(sendNotificationId);

        Assertions.assertNull(response);
    }
}