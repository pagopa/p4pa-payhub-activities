package it.gov.pagopa.payhub.activities.connector.sendnotification.config;

import it.gov.pagopa.payhub.activities.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.sendnotification.controller.ApiClient;
import it.gov.pagopa.pu.sendnotification.controller.BaseApi;
import it.gov.pagopa.pu.sendnotification.controller.generated.NotificationApi;
import it.gov.pagopa.pu.sendnotification.controller.generated.SendApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class SendApisHolder {

    private final RestTemplate restTemplate;
    private final SendApiClientConfig clientConfig;

    private final SendApi sendApi;
    private final NotificationApi sendNotificationAPI;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public SendApisHolder(
        SendApiClientConfig clientConfig,
        RestTemplateBuilder restTemplateBuilder
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.clientConfig = clientConfig;
        ApiClient apiClient = buildApiClient();

        if (clientConfig.isPrintBodyWhenError()) {
          restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("SEND_NOTIFICATION"));
        }

        this.sendApi = new SendApi(apiClient);
        this.sendNotificationAPI = new NotificationApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link SendApi} instrumented with the provided accessToken. Use null if auth is not required */
    public SendApi getSendApi(String accessToken){
        return getApi(accessToken, sendApi);
    }

    /** It will return a {@link NotificationApi} instrumented with the provided accessToken. Use null if auth is not required */
    public NotificationApi getSendNotificationApi(String accessToken){
        return getApi(accessToken, sendNotificationAPI);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }

    private ApiClient buildApiClient() {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        return apiClient;
    }
}
