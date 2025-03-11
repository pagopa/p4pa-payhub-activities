package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.ionotification.client.IoNotificationClient;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class IONotificationClientServiceImpl implements IONotificationClientService {

    private final IoNotificationClient ioNotificationClient;
    private final AuthnService authnService;


    public IONotificationClientServiceImpl(IoNotificationClient ioNotificationClient, AuthnService authnService) {
        this.ioNotificationClient = ioNotificationClient;
        this.authnService = authnService;
    }


    @Override
    public MessageResponseDTO sendMessage(NotificationRequestDTO notificationRequestDTO) {
        log.info("Sending message to IONotification for debt position type org {}", notificationRequestDTO.getDebtPositionTypeOrgId());
        String accessToken = authnService.getAccessToken();
        return ioNotificationClient.sendMessage(notificationRequestDTO, accessToken);
    }
}
