package it.gov.pagopa.payhub.activities.connector.sendnotification.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
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
class SendApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private SendApisHolder sendApisHolder;
    private SendApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = SendApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();

        sendApisHolder = new SendApisHolder(apiClientConfig, restTemplateBuilderMock);
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
                accessToken -> sendApisHolder.getSendStreamsApi(accessToken)
                        .getStream("sendStreamId"),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetSendApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> { sendApisHolder.getSendApi(accessToken)
                        .preloadSendFile("notificationId");
                    return voidMock;
                },
                new ParameterizedTypeReference<>() {},
                sendApisHolder::unload);
    }

    @Test
    void whenGetSendNotificationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> sendApisHolder.getSendNotificationApi(accessToken)
                        .getSendNotification("notificationId"),
                new ParameterizedTypeReference<>() {},
                sendApisHolder::unload);
    }

    @Test
    void whenGetSendStreamsApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> sendApisHolder.getSendStreamsApi(accessToken)
                        .getStream("sendStreamId"),
                new ParameterizedTypeReference<>() {},
                sendApisHolder::unload);
    }
}
