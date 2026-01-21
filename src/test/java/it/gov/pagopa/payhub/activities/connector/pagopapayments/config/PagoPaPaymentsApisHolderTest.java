package it.gov.pagopa.payhub.activities.connector.pagopapayments.config;

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

	private PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder;

	@BeforeEach
	void setUp() {
		when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
		when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
		PagoPaPaymentsApiClientConfig clientConfig = PagoPaPaymentsApiClientConfig.builder()
				.baseUrl("http://example.com")
				.build();
		pagoPaPaymentsApisHolder = new PagoPaPaymentsApisHolder(clientConfig, restTemplateBuilderMock);
	}

	@AfterEach
	void tearDown() {
		Mockito.verifyNoMoreInteractions(
			restTemplateBuilderMock,
			restTemplateMock
		);
	}

	@Test
	void whenCreateAcaApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
			accessToken -> {
				pagoPaPaymentsApisHolder.getAcaApi(accessToken)
					.syncAca("IUD", buildDebtPositionDTO());
				return voidMock;
			},
			new ParameterizedTypeReference<>() {},
			pagoPaPaymentsApisHolder::unload);
	}

	@Test
	void whenGetPaymentsReportingApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
			accessToken -> pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
					.restGetPaymentsReportingList(1L, OffsetDateTime.now()),
			new ParameterizedTypeReference<>() {},
			pagoPaPaymentsApisHolder::unload);
	}

	@Test
	void whenSyncGpdApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
				accessToken -> {
					pagoPaPaymentsApisHolder.getGpdApi(accessToken)
							.syncGpd("IUD", buildDebtPositionDTO());
					return voidMock;
				},
				new ParameterizedTypeReference<>() {},
				pagoPaPaymentsApisHolder::unload);
	}

	@Test
	void whenGenerateMassiveApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
				accessToken ->
					pagoPaPaymentsApisHolder.getPrintPaymentNoticeApi(accessToken)
							.generateMassive(new NoticeRequestMassiveDTO()),
				new ParameterizedTypeReference<>() {},
				pagoPaPaymentsApisHolder::unload);
	}
}