package it.gov.pagopa.payhub.activities.connector.ionotification.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
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
class IoNotificationApiHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private IoNotificationApisHolder apisHolder;
    private IoNotificationApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = IoNotificationApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();
        apisHolder = new IoNotificationApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getIoNotificationApi(null));
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
                accessToken -> apisHolder.getIoNotificationApi(accessToken)
                        .sendMessage(new NotificationRequestDTO()),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetIoNotificationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getIoNotificationApi(accessToken)
                            .sendMessage(new NotificationRequestDTO()),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
}
