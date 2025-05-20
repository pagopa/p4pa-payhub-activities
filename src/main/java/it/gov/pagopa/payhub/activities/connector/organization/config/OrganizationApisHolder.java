package it.gov.pagopa.payhub.activities.connector.organization.config;

import it.gov.pagopa.payhub.activities.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.organization.client.generated.*;
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
    private final BrokerSearchControllerApi brokerSearchControllerApi;
    private final OrganizationSearchControllerApi organizationSearchControllerApi;
    private final OrganizationEntityControllerApi organizationEntityControllerApi;
    private final OrganizationApi organizationApi;
    private final TaxonomyApi taxonomyApi;

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
        this.brokerSearchControllerApi = new BrokerSearchControllerApi(apiClient);
        this.organizationSearchControllerApi = new OrganizationSearchControllerApi(apiClient);
        this.organizationEntityControllerApi = new OrganizationEntityControllerApi(apiClient);
        this.taxonomyApi = new TaxonomyApi(apiClient);
        this.organizationApi = new OrganizationApi(apiClient);
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

    /** It will return a {@link BrokerSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
    public BrokerSearchControllerApi getBrokerSearchControllerApi(String accessToken){
        return getApi(accessToken, brokerSearchControllerApi);
    }

    /**
     * It will return a {@link TaxonomyApi} instrumented with the provided accessToken. Use null if auth is not required.
     */
    public TaxonomyApi getTaxonomyApi(String accessToken){
        return getApi(accessToken, taxonomyApi);
    }

    /**
     * It will return a {@link OrganizationApi} instrumented with the provided accessToken. Use null if auth is not required.
     */
    public OrganizationApi getOrganizationApi(String accessToken){
        return getApi(accessToken, organizationApi);
    }


    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
