package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.connector.ionotification.client.IoNotificationClient;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationQueueMapper;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.pu.p4paionotification.dto.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IoNotificationClient ioNotificationClient;
    private final NotificationQueueMapper notificationQueueMapper;

    public IONotificationServiceImpl(IoNotificationClient ioNotificationClient, NotificationQueueMapper notificationQueueMapper) {
        this.ioNotificationClient = ioNotificationClient;
        this.notificationQueueMapper = notificationQueueMapper;
    }


    @Override
    public void sendMessage(DebtPositionDTO debtPositionDTO) {
        for (NotificationQueueDTO notificationQueueDTO : notificationQueueMapper.mapDebtPositionDTO2NotificationQueueDTO(debtPositionDTO)) {
            log.info("Sending message to IONotification for debt position type org {}", notificationQueueDTO.getTipoDovutoId());
            ioNotificationClient.sendMessage(notificationQueueDTO);
        }
    }
}
