package it.gov.pagopa.payhub.activities.connector.ionotification.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;
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

@ExtendWith(MockitoExtension.class)
class IoNotificationApiHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private IoNotificationApisHolder ioNotificationApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ApiClient apiClient = new ApiClient(restTemplateMock);
        String baseUrl = "http://example.com";
        apiClient.setBasePath(baseUrl);
        ioNotificationApisHolder = new IoNotificationApisHolder(baseUrl, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenGetIoNotificationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    ioNotificationApisHolder.getIoNotificationApi(accessToken)
                            .sendMessage(new NotificationQueueDTO());
                    return null;
                },
                String.class,
                ioNotificationApisHolder::unload);
    }

}
