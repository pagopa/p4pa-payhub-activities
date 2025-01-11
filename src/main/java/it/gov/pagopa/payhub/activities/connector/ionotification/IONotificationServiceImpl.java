package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.ionotification.client.IoNotificationClient;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationQueueMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IoNotificationClient ioNotificationClient;
    private final NotificationQueueMapper notificationQueueMapper;
    private final AuthnService authnService;

    public IONotificationServiceImpl(IoNotificationClient ioNotificationClient, NotificationQueueMapper notificationQueueMapper, AuthnService authnService) {
        this.ioNotificationClient = ioNotificationClient;
        this.notificationQueueMapper = notificationQueueMapper;
        this.authnService = authnService;
    }


    @Override
    public void sendMessage(DebtPositionDTO debtPositionDTO) {
        for (NotificationQueueDTO notificationQueueDTO : notificationQueueMapper.mapDebtPositionDTO2NotificationQueueDTO(debtPositionDTO)) {
            log.info("Sending message to IONotification for debt position type org {}", notificationQueueDTO.getTipoDovutoId());
            String accessToken = authnService.getAccessToken();
            ioNotificationClient.sendMessage(notificationQueueDTO, accessToken);
        }
    }
}
