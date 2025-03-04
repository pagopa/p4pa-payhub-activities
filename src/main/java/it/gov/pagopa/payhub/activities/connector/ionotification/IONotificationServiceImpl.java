package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.ionotification.client.IoNotificationClient;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IoNotificationClient ioNotificationClient;
    private final NotificationRequestMapper notificationRequestMapper;
    private final AuthnService authnService;

    public IONotificationServiceImpl(IoNotificationClient ioNotificationClient, NotificationRequestMapper notificationRequestMapper, AuthnService authnService) {
        this.ioNotificationClient = ioNotificationClient;
        this.notificationRequestMapper = notificationRequestMapper;
        this.authnService = authnService;
    }


    @Override
    public void sendMessage(DebtPositionDTO debtPositionDTO) {
        // TODO https://pagopa.atlassian.net/browse/P4ADEV-2089
        for (NotificationRequestDTO notificationQueueDTO : notificationRequestMapper.mapDebtPositionDTO2NotificationRequestDTO(debtPositionDTO, "serviceId", "subject", "markdown")) {
            log.info("Sending message to IONotification for debt position type org {}", notificationQueueDTO.getDebtPositionTypeOrgId());
            String accessToken = authnService.getAccessToken();
            ioNotificationClient.sendMessage(notificationQueueDTO, accessToken);
        }
    }
}
