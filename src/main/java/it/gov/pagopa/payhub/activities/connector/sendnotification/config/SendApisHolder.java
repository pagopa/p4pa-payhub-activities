package it.gov.pagopa.payhub.activities.connector.sendnotification.config;

import it.gov.pagopa.payhub.activities.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.payhub.activities.connector.sendnotification.mapper.SendNotificationErrorDTOMapper;
import it.gov.pagopa.pu.sendnotification.generated.ApiClient;
import it.gov.pagopa.pu.sendnotification.generated.BaseApi;
import it.gov.pagopa.pu.sendnotification.client.generated.CampaignApi;
import it.gov.pagopa.pu.sendnotification.client.generated.NotificationApi;
import it.gov.pagopa.pu.sendnotification.client.generated.SendApi;
import it.gov.pagopa.pu.sendnotification.client.generated.StreamsApi;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationErrorDTO;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Lazy
@Service
public class SendApisHolder {

    private final RestTemplate restTemplate;
    private final SendApiClientConfig clientConfig;

    private final SendApi sendApi;
    private final NotificationApi sendNotificationAPI;
    private final StreamsApi streamsApi;
    private final CampaignApi campaignApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public SendApisHolder(
        SendApiClientConfig clientConfig,
        RestTemplateBuilder restTemplateBuilder,
        JsonMapper jsonMapper
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.clientConfig = clientConfig;
        ApiClient apiClient = buildApiClient();

        restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "SEND-NOTIFICATION", clientConfig.isPrintBodyWhenError(),
                SendNotificationErrorDTO.class, SendNotificationErrorDTOMapper::map)
        );

        this.sendApi = new SendApi(apiClient);
        this.sendNotificationAPI = new NotificationApi(apiClient);
        this.streamsApi = new StreamsApi(apiClient);
        this.campaignApi = new CampaignApi(apiClient);
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

    /** It will return a {@link StreamsApi} instrumented with the provided accessToken. Use null if auth is not required */
    public StreamsApi getSendStreamsApi(String accessToken){
        return getApi(accessToken, streamsApi);
    }

    /** It will return a {@link CampaignApi} instrumented with the provided accessToken. Use null if auth is not required */
    public CampaignApi getCampaignApi(String accessToken) {
        return getApi(accessToken, campaignApi);
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
