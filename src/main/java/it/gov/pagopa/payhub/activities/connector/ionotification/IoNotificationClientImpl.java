package it.gov.pagopa.payhub.activities.connector.ionotification;

import it.gov.pagopa.pu.p4paionotification.generated.ApiClient;
import it.gov.pagopa.pu.p4paionotification.client.generated.IoNotificationApi;
import it.gov.pagopa.pu.p4paionotification.dto.generated.NotificationQueueDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class IoNotificationClientImpl implements IoNotificationClient{

    private final IoNotificationApi ioNotificationApi;

    public IoNotificationClientImpl(
            @Value("${rest.io-notification.base-url}") String baseUrl,

            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        ioNotificationApi = new IoNotificationApi(apiClient);
    }

    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        ioNotificationApi.sendMessage(notificationQueueDTO);
    }
}
