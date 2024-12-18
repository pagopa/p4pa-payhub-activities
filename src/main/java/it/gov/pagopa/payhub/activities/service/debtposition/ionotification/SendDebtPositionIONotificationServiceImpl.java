package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.pu.p4paionotification.controller.ApiClient;
import it.gov.pagopa.pu.p4paionotification.controller.generated.IoNotificationApi;
import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Lazy
@Service
@Slf4j
public class SendDebtPositionIONotificationServiceImpl implements SendDebtPositionIONotificationService {

    private final IoNotificationApi ioNotificationApi;

    public SendDebtPositionIONotificationServiceImpl(RestTemplateBuilder restTemplateBuilder, String baseUrl) {
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
