package it.gov.pagopa.payhub.activities.connector.ionotification.client;

import it.gov.pagopa.payhub.activities.connector.ionotification.config.IoNotificationApisHolder;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class IoNotificationClient {

    private final IoNotificationApisHolder ioNotificationApisHolder;

    public IoNotificationClient(IoNotificationApisHolder ioNotificationApisHolder) {
        this.ioNotificationApisHolder = ioNotificationApisHolder;
    }

    public void sendMessage(NotificationRequestDTO notificationQueueDTO, String accessToken) {
        ioNotificationApisHolder.getIoNotificationApi(accessToken)
                .sendMessage(notificationQueueDTO);
    }
}
