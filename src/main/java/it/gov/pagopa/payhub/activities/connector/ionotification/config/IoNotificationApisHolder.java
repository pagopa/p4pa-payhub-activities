package it.gov.pagopa.payhub.activities.connector.ionotification.config;

import it.gov.pagopa.payhub.activities.config.RestTemplateConfig;
import it.gov.pagopa.pu.ionotification.client.generated.IoNotificationApi;
import it.gov.pagopa.pu.ionotification.generated.ApiClient;
import it.gov.pagopa.pu.ionotification.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class IoNotificationApisHolder {

    private final IoNotificationApi ioNotificationApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public IoNotificationApisHolder(
            IoNotificationClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("ORGANIZATION"));
        }

        this.ioNotificationApi = new IoNotificationApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link IoNotificationApi} instrumented with the provided accessToken. Use null if auth is not required */
    public IoNotificationApi getIoNotificationApi(String accessToken){
        return getApi(accessToken, ioNotificationApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
