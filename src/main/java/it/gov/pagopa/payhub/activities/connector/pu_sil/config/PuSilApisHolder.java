package it.gov.pagopa.payhub.activities.connector.pu_sil.config;

import it.gov.pagopa.payhub.activities.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.pu.pusil.generated.ApiClient;
import it.gov.pagopa.pu.pusil.generated.BaseApi;
import it.gov.pagopa.pu.pusil.client.generated.NotifyPaymentApi;
import it.gov.pagopa.pu.pusil.dto.generated.PuSilErrorDTO;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Service
public class PuSilApisHolder {

  private final NotifyPaymentApi notifyPaymentApi;
  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();


  public PuSilApisHolder(
    PuSilApiClientConfig clientConfig,
    RestTemplateBuilder restTemplateBuilder,
    JsonMapper jsonMapper
  ) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(clientConfig.getBaseUrl());
    apiClient.setBearerToken(bearerTokenHolder::get);
    apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
    apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
    restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "PU-SIL", clientConfig.isPrintBodyWhenError(),
            PuSilErrorDTO.class, PuSilErrorDTO::getCode, PuSilErrorDTO::getMessage)
    );

    this.notifyPaymentApi = new NotifyPaymentApi(apiClient);
  }

  @PreDestroy
  public void unload(){
    bearerTokenHolder.remove();
  }

  public NotifyPaymentApi getNotifyPaymentApi(String accessToken) {
    return getApi(accessToken, notifyPaymentApi);
  }

  private <T extends BaseApi> T getApi(String accessToken, T api) {
    bearerTokenHolder.set(accessToken);
    return api;
  }
}
