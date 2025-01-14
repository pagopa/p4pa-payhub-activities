package it.gov.pagopa.payhub.activities.connector.aca.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.ionotification.generated.ApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildPaymentsDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class AcaApisHoldetTest  extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private AcaApisHolder acaApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ApiClient apiClient = new ApiClient(restTemplateMock);
        String baseUrl = "http://example.com";
        apiClient.setBasePath(baseUrl);
        acaApisHolder = new AcaApisHolder(baseUrl, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenCreateAcaApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    acaApisHolder.getAcaApi(accessToken)
                            .createAca(buildPaymentsDebtPositionDTO());
                    return null;
                },
                String.class,
                acaApisHolder::unload);
    }
}
