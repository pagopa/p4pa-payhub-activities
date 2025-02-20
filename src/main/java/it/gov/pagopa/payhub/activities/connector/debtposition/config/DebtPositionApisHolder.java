package it.gov.pagopa.payhub.activities.connector.debtposition.config;

import it.gov.pagopa.payhub.activities.config.RestTemplateConfig;
import it.gov.pagopa.pu.debtposition.client.generated.*;
import it.gov.pagopa.pu.debtposition.generated.ApiClient;
import it.gov.pagopa.pu.debtposition.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class DebtPositionApisHolder {
    private final TransferSearchControllerApi transferSearchControllerApi;
    private final TransferApi transferApi;
    private final DebtPositionSearchControllerApi debtPositionSearchControllerApi;
    private final DebtPositionApi debtPositionApi;
    private final ReceiptApi receiptApi;
    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public DebtPositionApisHolder(
        DebtPositionApiClientConfig clientConfig,
        RestTemplateBuilder restTemplateBuilder
    ) {
	    RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("DEBT-POSITIONS"));
        }

        this.debtPositionSearchControllerApi = new DebtPositionSearchControllerApi(apiClient);
        this.debtPositionApi = new DebtPositionApi(apiClient);
        this.transferSearchControllerApi = new TransferSearchControllerApi(apiClient);
        this.transferApi = new TransferApi(apiClient);
        this.receiptApi = new ReceiptApi(apiClient);
    }

    @PreDestroy
    public void unload() {
        bearerTokenHolder.remove();
    }

    /**
     * It will return a {@link DebtPositionSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required
     */
    public DebtPositionSearchControllerApi getDebtPositionSearchControllerApi(String accessToken) {
        return getApi(accessToken, debtPositionSearchControllerApi);
    }

    public DebtPositionApi getDebtPositionApi(String accessToken){
        return getApi(accessToken, debtPositionApi);
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

    public ReceiptApi getReceiptApi(String accessToken){
        return getApi(accessToken, receiptApi);
    }


    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
