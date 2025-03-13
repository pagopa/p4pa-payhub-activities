package it.gov.pagopa.payhub.activities.connector.debtposition.config;

import it.gov.pagopa.payhub.activities.config.RestTemplateConfig;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionApi;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionSearchControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.ReceiptApi;
import it.gov.pagopa.pu.debtposition.client.generated.ReceiptNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.TransferApi;
import it.gov.pagopa.pu.debtposition.client.generated.TransferSearchControllerApi;
import it.gov.pagopa.pu.debtposition.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.tuple.Pair;
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
    private final DebtPositionTypeOrgApi debtPositionTypeOrgApi;
    private final ReceiptNoPiiSearchControllerApi receiptNoPiiSearchControllerApi;
    private final InstallmentNoPiiSearchControllerApi installmentNoPiiSearchControllerApi;
    /** it will store the actual accessToken and mappedExternalUserId */
    private final ThreadLocal<Pair<String, String>> authContextHolder = new ThreadLocal<>();

    public DebtPositionApisHolder(
            DebtPositionApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClientExt apiClient = new ApiClientExt(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(() -> authContextHolder.get().getKey());
        apiClient.setUserIdSupplier(() -> authContextHolder.get().getValue());
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
        this.debtPositionTypeOrgApi = new DebtPositionTypeOrgApi(apiClient);
	      this.receiptNoPiiSearchControllerApi = new ReceiptNoPiiSearchControllerApi(apiClient);
        this.installmentNoPiiSearchControllerApi = new InstallmentNoPiiSearchControllerApi(apiClient);
    }

    @PreDestroy
    public void unload() {
        authContextHolder.remove();
    }

    public DebtPositionSearchControllerApi getDebtPositionSearchControllerApi(String accessToken) {
        return getApi(accessToken, null, debtPositionSearchControllerApi);
    }

    public DebtPositionApi getDebtPositionApi(String accessToken){
        return getDebtPositionApi(accessToken, null);
    }

    public DebtPositionApi getDebtPositionApi(String accessToken, String mappedExternalUserId){
        return getApi(accessToken, mappedExternalUserId, debtPositionApi);
    }

    public TransferSearchControllerApi getTransferSearchControllerApi(String accessToken) {
        return getApi(accessToken, null, transferSearchControllerApi);
    }

    public TransferApi getTransferApi(String accessToken){
        return getApi(accessToken, null, transferApi);
    }

    public ReceiptApi getReceiptApi(String accessToken){
        return getApi(accessToken, null, receiptApi);
    }

    public DebtPositionTypeOrgApi getDebtPositionTypeOrgApi(String accessToken) {
        return getApi(accessToken, null, debtPositionTypeOrgApi);
    }

	  public ReceiptNoPiiSearchControllerApi getReceiptNoPiiSearchControllerApi(String accessToken) {
        return getApi(accessToken, null, receiptNoPiiSearchControllerApi);
    }

    public InstallmentNoPiiSearchControllerApi getInstallmentNoPIISearchControllerApi(String accessToken) {
        return getApi(accessToken, null, installmentNoPiiSearchControllerApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, String mappedExternalUserId, T api) {
        authContextHolder.set(Pair.of(accessToken, mappedExternalUserId));
        return api;
    }
}
