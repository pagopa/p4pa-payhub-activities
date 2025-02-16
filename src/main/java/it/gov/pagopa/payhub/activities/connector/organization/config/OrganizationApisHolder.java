package it.gov.pagopa.payhub.activities.connector.organization.config;

import it.gov.pagopa.payhub.activities.config.RestTemplateConfig;
import it.gov.pagopa.pu.organization.client.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.organization.client.generated.OrganizationEntityControllerApi;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.generated.ApiClient;
import it.gov.pagopa.pu.organization.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class OrganizationApisHolder {
    private final BrokerEntityControllerApi brokerEntityControllerApi;
    private final OrganizationSearchControllerApi organizationSearchControllerApi;
    private final OrganizationEntityControllerApi organizationEntityControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public OrganizationApisHolder(
        OrganizationApiClientConfig clientConfig,
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

        this.brokerEntityControllerApi = new BrokerEntityControllerApi(apiClient);
        this.organizationSearchControllerApi = new OrganizationSearchControllerApi(apiClient);
        this.organizationEntityControllerApi = new OrganizationEntityControllerApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link OrganizationSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
    public OrganizationSearchControllerApi getOrganizationSearchControllerApi(String accessToken){
        return getApi(accessToken, organizationSearchControllerApi);
    }
    /** It will return a {@link OrganizationEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
    public OrganizationEntityControllerApi getOrganizationEntityControllerApi(String accessToken){
        return getApi(accessToken, organizationEntityControllerApi);
    }

    /** It will return a {@link BrokerEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
    public BrokerEntityControllerApi getBrokerEntityControllerApi(String accessToken){
        return getApi(accessToken, brokerEntityControllerApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
