package it.gov.pagopa.payhub.activities.connector.transfer.config;

import it.gov.pagopa.pu.debtposition.client.generated.TransferApi;
import it.gov.pagopa.pu.debtposition.client.generated.TransferSearchControllerApi;
import it.gov.pagopa.pu.debtposition.generated.ApiClient;
import it.gov.pagopa.pu.debtposition.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class TransferApisHolder {
    private final TransferSearchControllerApi transferSearchControllerApi;
    private final TransferApi transferApi;
    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public TransferApisHolder(
        @Value("${rest.debt-position.base-url}") String baseUrl,
        RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        this.transferSearchControllerApi = new TransferSearchControllerApi(apiClient);
        this.transferApi = new TransferApi(apiClient);
    }

    @PreDestroy
    public void unload() {
        bearerTokenHolder.remove();
    }

    /**
     * It will return a {@link TransferSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required
     */
    public TransferSearchControllerApi getTransferSearchControllerApi(String accessToken) {
        return getApi(accessToken, transferSearchControllerApi);
    }

    public TransferApi getTransferApi(String accessToken){
        return getApi(accessToken, transferApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
