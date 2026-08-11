package it.gov.pagopa.payhub.activities.connector.pagopapayments.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.pagopapayments.dto.generated.NoticeRequestMassiveDTO;
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

import java.time.OffsetDateTime;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoPaPaymentsApisHolderTest extends BaseApiHolderTest {
	@Mock
	private RestTemplateBuilder restTemplateBuilderMock;

	private PagoPaPaymentsApisHolder apisHolder;
	private PagoPaPaymentsApiClientConfig apiClientConfig;

	@BeforeEach
	void init() {
		when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
		when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

		apiClientConfig = PagoPaPaymentsApiClientConfig.builder()
				.baseUrl("http://example.com")
				.maxAttempts(3)
				.build();
		apisHolder = new PagoPaPaymentsApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

		verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getAcaApi(null));
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
				accessToken -> apisHolder.getPrintPaymentNoticeApi(accessToken)
						.generateMassive(new NoticeRequestMassiveDTO()),
				new ParameterizedTypeReference<>() {}
		);
	}

	@Test
	void whenCreateAcaApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
			accessToken -> {
				apisHolder.getAcaApi(accessToken)
					.syncAca("IUD", buildDebtPositionDTO(), Boolean.FALSE);
				return voidMock;
			},
			new ParameterizedTypeReference<>() {},
			apisHolder::unload);
	}

	@Test
	void whenGetPaymentsReportingApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
			accessToken -> apisHolder.getPaymentsReportingApi(accessToken)
					.getPaymentsReportingList(1L, OffsetDateTime.now()),
			new ParameterizedTypeReference<>() {},
			apisHolder::unload);
	}

	@Test
	void whenSyncGpdApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
				accessToken -> {
					apisHolder.getGpdApi(accessToken)
							.syncGpd("IUD", buildDebtPositionDTO());
					return voidMock;
				},
				new ParameterizedTypeReference<>() {},
				apisHolder::unload);
	}

	@Test
	void whenGenerateMassiveApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
				accessToken ->
					apisHolder.getPrintPaymentNoticeApi(accessToken)
							.generateMassive(new NoticeRequestMassiveDTO()),
				new ParameterizedTypeReference<>() {},
				apisHolder::unload);
	}
}