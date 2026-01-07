package it.gov.pagopa.payhub.activities.connector.pagopapayments.config;

import it.gov.pagopa.payhub.activities.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.pagopapayments.client.generated.AcaApi;
import it.gov.pagopa.pu.pagopapayments.client.generated.GpdApi;
import it.gov.pagopa.pu.pagopapayments.client.generated.PaymentsReportingApi;
import it.gov.pagopa.pu.pagopapayments.client.generated.PrintPaymentNoticeApi;
import it.gov.pagopa.pu.pagopapayments.generated.ApiClient;
import it.gov.pagopa.pu.pagopapayments.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class PagoPaPaymentsApisHolder {
    private final AcaApi acaApi;
    private final PaymentsReportingApi paymentsReportingApi;
    private final GpdApi gpdApi;
    private final PrintPaymentNoticeApi printPaymentNoticeApi;
    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public PagoPaPaymentsApisHolder(
            PagoPaPaymentsApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("PAGOPA-PAYMENTS"));
        }

        this.acaApi = new AcaApi(apiClient);
        this.paymentsReportingApi = new PaymentsReportingApi(apiClient);
        this.gpdApi = new GpdApi(apiClient);
        this.printPaymentNoticeApi = new PrintPaymentNoticeApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link AcaApi} instrumented with the provided accessToken. Use null if auth is not required */
    public AcaApi getAcaApi(String accessToken){
        return getApi(accessToken, acaApi);
    }

    /** It will return a {@link PaymentsReportingApi} instrumented with the provided accessToken. Use null if auth is not required */
    public PaymentsReportingApi getPaymentsReportingApi(String accessToken){
        return getApi(accessToken, paymentsReportingApi);
    }

    /** It will return a {@link GpdApi} instrumented with the provided accessToken. Use null if auth is not required */
    public GpdApi getGpdApi(String accessToken) {
        return getApi(accessToken, gpdApi);
    }

    /** It will return a {@link PrintPaymentNoticeApi} instrumented with the provided accessToken. Use null if auth is not required */
    public PrintPaymentNoticeApi getPrintPaymentNoticeApi(String accessToken) {
        return getApi(accessToken, printPaymentNoticeApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
