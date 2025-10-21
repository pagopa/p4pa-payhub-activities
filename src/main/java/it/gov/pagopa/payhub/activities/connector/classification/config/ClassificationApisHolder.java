package it.gov.pagopa.payhub.activities.connector.classification.config;

import it.gov.pagopa.payhub.activities.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.classification.client.generated.*;
import it.gov.pagopa.pu.classification.generated.ApiClient;
import it.gov.pagopa.pu.classification.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class ClassificationApisHolder {

    private final ClassificationSearchControllerApi classificationSearchControllerApi;
    private final ClassificationEntityControllerApi classificationEntityControllerApi;
    private final ClassificationEntityExtendedControllerApi classificationEntityExtendedControllerApi;

    private final PaymentsReportingSearchControllerApi paymentsReportingSearchControllerApi;
    private final PaymentsReportingEntityControllerApi paymentsReportingEntityControllerApi;
    private final PaymentsReportingEntityExtendedControllerApi paymentsReportingEntityExtendedControllerApi;

    private final TreasurySearchControllerApi treasurySearchControllerApi;
    private final TreasuryEntityControllerApi treasuryEntityControllerApi;
    private final TreasuryEntityExtendedControllerApi treasuryEntityExtendedControllerApi;

    private final AssessmentsControllerApi assessmentsControllerApi;
    private final AssessmentsSearchControllerApi assessmentsSearchControllerApi;
    private final AssessmentsEntityControllerApi assessmentsEntityControllerApi;

    private final AssessmentsDetailEntityControllerApi assessmentsDetailEntityControllerApi;
    private final AssessmentsDetailSearchControllerApi assessmentsDetailSearchControllerApi;

    private final AssessmentsRegistryApi assessmentsRegistryApi;
    private final AssessmentsRegistrySearchControllerApi assessmentsRegistrySearchControllerApi;

    private final PaymentNotificationApi paymentNotificationApi;
    private final PaymentNotificationNoPiiSearchControllerApi paymentNotificationNoPiiSearchControllerApi;
    private final DataExportsApi dataExportsApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public ClassificationApisHolder(
            ClassificationApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder
    ) {
      RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("CLASSIFICATION"));
        }

        this.classificationSearchControllerApi = new ClassificationSearchControllerApi(apiClient);
        this.classificationEntityControllerApi = new ClassificationEntityControllerApi(apiClient);
        this.classificationEntityExtendedControllerApi = new ClassificationEntityExtendedControllerApi(apiClient);

        this.paymentsReportingSearchControllerApi = new PaymentsReportingSearchControllerApi(apiClient);
        this.paymentsReportingEntityControllerApi = new PaymentsReportingEntityControllerApi(apiClient);
        this.paymentsReportingEntityExtendedControllerApi = new PaymentsReportingEntityExtendedControllerApi(apiClient);

        this.treasurySearchControllerApi = new TreasurySearchControllerApi(apiClient);
        this.treasuryEntityControllerApi = new TreasuryEntityControllerApi(apiClient);
        this.treasuryEntityExtendedControllerApi = new TreasuryEntityExtendedControllerApi(apiClient);

        this.assessmentsControllerApi = new AssessmentsControllerApi(apiClient);
        this.assessmentsSearchControllerApi = new AssessmentsSearchControllerApi(apiClient);
        this.assessmentsEntityControllerApi = new AssessmentsEntityControllerApi(apiClient);

        this.assessmentsDetailEntityControllerApi = new AssessmentsDetailEntityControllerApi(apiClient);
        this.assessmentsDetailSearchControllerApi = new AssessmentsDetailSearchControllerApi(apiClient);

        this.assessmentsRegistryApi = new AssessmentsRegistryApi(apiClient);
        this.assessmentsRegistrySearchControllerApi = new AssessmentsRegistrySearchControllerApi(apiClient);


        this.paymentNotificationApi = new PaymentNotificationApi(apiClient);
        this.paymentNotificationNoPiiSearchControllerApi = new PaymentNotificationNoPiiSearchControllerApi(apiClient);

        this.dataExportsApi = new DataExportsApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link TreasurySearchControllerApi} instrumented with the provided accessToken.*/

    public ClassificationSearchControllerApi getClassificationSearchControllerApi(String accessToken) {
        return getApi(accessToken, classificationSearchControllerApi);
    }

    public ClassificationEntityControllerApi getClassificationEntityControllerApi(String accessToken){
        return getApi(accessToken, classificationEntityControllerApi);
    }
    public ClassificationEntityExtendedControllerApi getClassificationEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, classificationEntityExtendedControllerApi);
    }

    public PaymentsReportingSearchControllerApi getPaymentsReportingSearchApi(String accessToken){
        return getApi(accessToken, paymentsReportingSearchControllerApi);
    }
    public PaymentsReportingEntityControllerApi getPaymentsReportingEntityControllerApi(String accessToken){
        return getApi(accessToken, paymentsReportingEntityControllerApi);
    }
    public PaymentsReportingEntityExtendedControllerApi getPaymentsReportingEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, paymentsReportingEntityExtendedControllerApi);
    }

    public TreasurySearchControllerApi getTreasurySearchApi(String accessToken){
        return getApi(accessToken, treasurySearchControllerApi);
    }
    public TreasuryEntityControllerApi getTreasuryEntityControllerApi(String accessToken){
        return getApi(accessToken, treasuryEntityControllerApi);
    }
    public TreasuryEntityExtendedControllerApi getTreasuryEntityExtendedControllerApi(String accessToken){
        return getApi(accessToken, treasuryEntityExtendedControllerApi);
    }

    public AssessmentsControllerApi getAssessmentsControllerApi(String accessToken){
        return getApi(accessToken, assessmentsControllerApi);
    }

    public AssessmentsSearchControllerApi getAssessmentsSearchControllerApi(String accessToken){
        return getApi(accessToken, assessmentsSearchControllerApi);
    }

    public AssessmentsEntityControllerApi getAssessmentsEntityControllerApi(String accessToken){
        return getApi(accessToken, assessmentsEntityControllerApi);
    }

    public AssessmentsDetailEntityControllerApi getAssessmentsDetailEntityControllerApi(String accessToken){
        return getApi(accessToken, assessmentsDetailEntityControllerApi);
    }

    public AssessmentsDetailSearchControllerApi getAssessmentsDetailSearchControllerApi(String accessToken){
        return getApi(accessToken, assessmentsDetailSearchControllerApi);
    }

    public AssessmentsRegistryApi getAssessmentsRegistryApi(String accessToken){
        return getApi(accessToken, assessmentsRegistryApi);
    }

    public AssessmentsRegistrySearchControllerApi getAssessmentsRegistrySearchControllerApi(String accessToken){
        return getApi(accessToken, assessmentsRegistrySearchControllerApi);
    }

    public PaymentNotificationApi getPaymentNotificationApi(String accessToken) {
      return getApi(accessToken, paymentNotificationApi);
    }

    public PaymentNotificationNoPiiSearchControllerApi getPaymentNotificationNoPiiSearchControllerApi(String accessToken){
        return getApi(accessToken, paymentNotificationNoPiiSearchControllerApi);
    }

    public DataExportsApi getDataExportsApi(String accessToken){
        return getApi(accessToken, dataExportsApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
