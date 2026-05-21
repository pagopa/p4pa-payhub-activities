package it.gov.pagopa.payhub.activities.connector.ionotification.config;

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

@ExtendWith(MockitoExtension.class)
class IoNotificationApiHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private IoNotificationApisHolder ioNotificationApisHolder;
    private IoNotificationApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = IoNotificationApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();

        ioNotificationApisHolder = new IoNotificationApisHolder(apiClientConfig, restTemplateBuilderMock);
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
                accessToken -> ioNotificationApisHolder.getIoNotificationApi(accessToken)
                        .sendMessage(new NotificationRequestDTO()),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetIoNotificationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> ioNotificationApisHolder.getIoNotificationApi(accessToken)
                            .sendMessage(new NotificationRequestDTO()),
                new ParameterizedTypeReference<>() {},
                ioNotificationApisHolder::unload);
    }
}
