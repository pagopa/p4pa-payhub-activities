package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadSendFileActivityTest {

    @Mock
    private SendService sendServiceMock;

    private UploadSendFileActivity uploadSendFileActivity;

    @BeforeEach
    void init() {
        uploadSendFileActivity = new UploadSendFileActivityImpl(sendServiceMock);
    }


    @Test
    void whenPreloadSendFileThenVoid() {
        uploadSendFileActivity.uploadSendFile("sendNotificationId");

        Mockito.verify(sendServiceMock).uploadSendFile("sendNotificationId");
    }

}