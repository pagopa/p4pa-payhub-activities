package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.ionotification.IoNotificationClient;
import it.gov.pagopa.pu.p4paionotification.dto.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
@Lazy
@Service
@Slf4j
public class SendDebtPositionIONotificationServiceImpl implements SendDebtPositionIONotificationService {

    private final IoNotificationClient ioNotificationClient;

    public SendDebtPositionIONotificationServiceImpl(IoNotificationClient ioNotificationClient) {
        this.ioNotificationClient = ioNotificationClient;
    }


    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        ioNotificationClient.sendMessage(notificationQueueDTO);
    }
}
