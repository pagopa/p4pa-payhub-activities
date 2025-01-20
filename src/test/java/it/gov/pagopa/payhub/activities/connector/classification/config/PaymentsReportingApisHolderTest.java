package it.gov.pagopa.payhub.activities.connector.classification.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReportingRequestBody;
import it.gov.pagopa.pu.classification.generated.ApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private PaymentsReportingApisHolder paymentsReportingApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ApiClient apiClient = new ApiClient(restTemplateMock);
        String baseUrl = "http://example.com";
        apiClient.setBasePath(baseUrl);
        paymentsReportingApisHolder = new PaymentsReportingApisHolder(baseUrl, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenGetPaymentsReportingSearchApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    paymentsReportingApisHolder.getPaymentsReportingSearchApi(accessToken)
                            .crudPaymentsReportingFindByOrganizationIdAndIuf(1L, "iuf");
                    return null;
                },
                String.class,
                paymentsReportingApisHolder::unload);
    }

    @Test
    void whenGetPaymentsReportingEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    paymentsReportingApisHolder.getPaymentsReportingEntityControllerApi(accessToken)
                            .crudCreatePaymentsreporting(new PaymentsReportingRequestBody());
                    return null;
                },
                String.class,
                paymentsReportingApisHolder::unload);
    }

    @Test
    void whenGetPaymentsReportingEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    paymentsReportingApisHolder.getPaymentsReportingEntityExtendedControllerApi(accessToken)
                            .saveAll1(List.of(new PaymentsReporting()));
                    return null;
                },
                String.class,
                paymentsReportingApisHolder::unload);
    }
}