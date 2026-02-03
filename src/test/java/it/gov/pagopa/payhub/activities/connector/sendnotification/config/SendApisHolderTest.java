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

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        SendApiClientConfig clientConfig = SendApiClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        sendApisHolder = new SendApisHolder(clientConfig, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
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
                        .getStreamByOrganizationId(1L),
                new ParameterizedTypeReference<>() {},
                sendApisHolder::unload);
    }
}
