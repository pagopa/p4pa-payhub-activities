package it.gov.pagopa.payhub.activities.connector.pu_sil.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
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
class PuSilApisHolderTest extends BaseApiHolderTest {

  @Mock
  private RestTemplateBuilder restTemplateBuilderMock;

  private PuSilApisHolder apisHolder;
  private PuSilApiClientConfig apiClientConfig;

  @BeforeEach
  void init() {
    when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
    when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

    apiClientConfig = PuSilApiClientConfig.builder()
        .baseUrl("http://example.com")
            .maxAttempts(3)
        .build();
    apisHolder = new PuSilApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

    verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getNotifyPaymentApi(null));
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
            accessToken -> { apisHolder.getNotifyPaymentApi(accessToken)
                    .notifyPayment(1L, new InstallmentDTO());
              return voidMock;
            },
            new ParameterizedTypeReference<>() {}
    );
  }

  @Test
  void whenGetNotifyPaymentApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
        accessToken -> { apisHolder.getNotifyPaymentApi(accessToken)
            .notifyPayment(1L, new InstallmentDTO());
          return voidMock;
        },
        new ParameterizedTypeReference<>() {},
        apisHolder::unload);
  }
}