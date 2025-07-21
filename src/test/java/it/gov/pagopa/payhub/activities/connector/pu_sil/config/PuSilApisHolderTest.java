package it.gov.pagopa.payhub.activities.connector.pu_sil.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

@ExtendWith(MockitoExtension.class)
class PuSilApisHolderTest extends BaseApiHolderTest {

  @Mock
  private RestTemplateBuilder restTemplateBuilderMock;

  private PuSilApisHolder puSilApisHolder;

  @BeforeEach
  void setUp() {
    Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
    Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
    PuSilApiClientConfig clientConfig = PuSilApiClientConfig.builder()
        .baseUrl("http://example.com")
        .build();
    puSilApisHolder = new PuSilApisHolder(clientConfig, restTemplateBuilderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        restTemplateBuilderMock,
        restTemplateMock
    );
  }

  @Test
  void whenGetNotifyPaymentApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
        accessToken -> { puSilApisHolder.getNotifyPaymentApi(accessToken)
            .notifyPayment(1L, new InstallmentDTO());
          return voidMock;
        },
        new ParameterizedTypeReference<>() {},
        puSilApisHolder::unload);
  }
}