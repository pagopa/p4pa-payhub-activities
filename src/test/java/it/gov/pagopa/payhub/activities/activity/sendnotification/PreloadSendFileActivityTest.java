package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreloadSendFileActivityTest {

    @Mock
    private SendService sendServiceMock;

    private PreloadSendFileActivity preloadSendFileActivity;

    @BeforeEach
    void init() {
        preloadSendFileActivity = new PreloadSendFileActivityImpl(sendServiceMock);
    }


    @Test
    void whenPreloadSendFileThenVoid() {
        preloadSendFileActivity.preloadSendFile("sendNotificationId");

        Mockito.verify(sendServiceMock).preloadSendFile("sendNotificationId");
    }

}