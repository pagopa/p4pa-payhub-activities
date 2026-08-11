package it.gov.pagopa.payhub.activities.connector.sendnotification.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private SendApisHolder apisHolder;
    private SendApiClientConfig apiClientConfig;

    @BeforeEach
    void init() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = SendApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();
        apisHolder = new SendApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getCampaignApi(null));
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
                accessToken -> apisHolder.getSendStreamsApi(accessToken)
                        .getStream("sendStreamId"),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetSendApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> { apisHolder.getSendApi(accessToken)
                        .preloadSendFile("notificationId");
                    return voidMock;
                },
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetSendNotificationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getSendNotificationApi(accessToken)
                        .getSendNotification("notificationId"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetSendStreamsApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getSendStreamsApi(accessToken)
                        .getStream("sendStreamId"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getCampaignApi(accessToken)
                        .fetchAllCampaignIds(),
                new ParameterizedTypeReference<>() {
                },
                apisHolder::unload
        );
    }
}
