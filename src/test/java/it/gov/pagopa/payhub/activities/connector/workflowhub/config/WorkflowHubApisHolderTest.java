package it.gov.pagopa.payhub.activities.connector.workflowhub.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import it.gov.pagopa.pu.workflowhub.dto.generated.SyncDebtPositionRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowHubApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private WorkflowHubApisHolder apisHolder;
    private WorkflowHubApiClientConfig apiClientConfig;

    @BeforeEach
    void init() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = WorkflowHubApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();
        apisHolder = new WorkflowHubApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getDebtPositionApi(null));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void testRetryConfiguration() {
        assertRetry(apiClientConfig,
                accessToken -> apisHolder.getDebtPositionApi(accessToken)
                        .syncDebtPosition(new SyncDebtPositionRequestDTO(), false, false, PaymentEventType.DP_CREATED, null),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetWorkflowHubApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getWorkflowHubApi(accessToken)
                            .getWorkflowStatus("workflowId"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getDebtPositionApi(accessToken)
                        .syncDebtPosition(new SyncDebtPositionRequestDTO(), false, false, PaymentEventType.DP_CREATED, null),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
}
