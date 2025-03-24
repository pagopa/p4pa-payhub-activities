package it.gov.pagopa.payhub.activities.connector.processexecutions.config;

import it.gov.pagopa.payhub.activities.config.RestTemplateConfig;
import it.gov.pagopa.pu.processexecutions.client.generated.ExportFileEntityControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.ExportFileEntityExtendedControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileEntityExtendedControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.IngestionFlowFileSearchControllerApi;
import it.gov.pagopa.pu.processexecutions.client.generated.PaidExportFileEntityControllerApi;
import it.gov.pagopa.pu.processexecutions.generated.ApiClient;
import it.gov.pagopa.pu.processexecutions.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class ProcessExecutionsApisHolder {

    private final IngestionFlowFileEntityControllerApi ingestionFlowFileEntityControllerApi;
    private final IngestionFlowFileEntityExtendedControllerApi ingestionFlowFileEntityExtendedControllerApi;
    private final IngestionFlowFileSearchControllerApi ingestionFlowFileSearchControllerApi;
    private final PaidExportFileEntityControllerApi paidExportFileEntityControllerApi;
    private final ExportFileEntityControllerApi exportFileEntityControllerApi;
    private final ExportFileEntityExtendedControllerApi exportFileEntityExtendedControllerApi;
    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public ProcessExecutionsApisHolder(
        ProcessExecutionsApiClientConfig clientConfig,
        RestTemplateBuilder restTemplateBuilder
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("PROCESS-EXECUTIONS"));
        }

        this.ingestionFlowFileEntityControllerApi = new IngestionFlowFileEntityControllerApi(apiClient);
        this.ingestionFlowFileEntityExtendedControllerApi = new IngestionFlowFileEntityExtendedControllerApi(apiClient);
        this.ingestionFlowFileSearchControllerApi = new IngestionFlowFileSearchControllerApi(apiClient);
        this.paidExportFileEntityControllerApi = new PaidExportFileEntityControllerApi(apiClient);
        this.exportFileEntityControllerApi = new ExportFileEntityControllerApi(apiClient);
        this.exportFileEntityExtendedControllerApi = new ExportFileEntityExtendedControllerApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }


    public IngestionFlowFileEntityControllerApi getIngestionFlowFileEntityControllerApi(String accessToken){
        return getApi(accessToken, ingestionFlowFileEntityControllerApi);
    }
    public IngestionFlowFileEntityExtendedControllerApi getIngestionFlowFileEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, ingestionFlowFileEntityExtendedControllerApi);
    }

    public IngestionFlowFileSearchControllerApi getIngestionFlowFileSearchControllerApi(String accessToken){
        return getApi(accessToken, ingestionFlowFileSearchControllerApi);
    }

    public PaidExportFileEntityControllerApi getPaidExportFileEntityControllerApi(String accessToken){
        return getApi(accessToken, paidExportFileEntityControllerApi);
    }

    public ExportFileEntityControllerApi getExportFileEntityControllerApi(String accessToken){
        return getApi(accessToken, exportFileEntityControllerApi);
    }

    public ExportFileEntityExtendedControllerApi getExportFileEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, exportFileEntityExtendedControllerApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
