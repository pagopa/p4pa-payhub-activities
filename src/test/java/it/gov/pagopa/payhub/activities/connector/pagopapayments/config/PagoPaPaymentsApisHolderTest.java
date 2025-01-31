package it.gov.pagopa.payhub.activities.connector.pagopapayments.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.processexecutions.generated.ApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildPaymentsDebtPositionDTO;
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
		ApiClient apiClient = new ApiClient(restTemplateMock);
		String baseUrl = "http://example.com";
		apiClient.setBasePath(baseUrl);
		pagoPaPaymentsApisHolder = new PagoPaPaymentsApisHolder(baseUrl, restTemplateBuilderMock);
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
					.syncAca("IUD", buildPaymentsDebtPositionDTO());
				return null;
			},
			String.class,
			pagoPaPaymentsApisHolder::unload);
	}

	@Test
	void whenGetPaymentsReportingApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
			accessToken -> {
				pagoPaPaymentsApisHolder.getPaymentsReportingApi(accessToken)
					.getPaymentsReportingList(1L);
				return null;
			},
			String.class,
			pagoPaPaymentsApisHolder::unload);
	}
}