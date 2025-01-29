package it.gov.pagopa.payhub.activities.connector.transfer.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.debtposition.generated.ApiClient;
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
class TransferApisHolderTest extends BaseApiHolderTest {
	@Mock
	private RestTemplateBuilder restTemplateBuilderMock;

	private TransferApisHolder transferApisHolder;

	@BeforeEach
	void setUp() {
		Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
		Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
		ApiClient apiClient = new ApiClient(restTemplateMock);
		String baseUrl = "http://example.com";
		apiClient.setBasePath(baseUrl);
		transferApisHolder = new TransferApisHolder(baseUrl, restTemplateBuilderMock);
	}

	@AfterEach
	void tearDown() { Mockito.verifyNoMoreInteractions(restTemplateBuilderMock, restTemplateMock); }

	@Test
	void whenGetTransferSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
			accessToken -> transferApisHolder.getTransferSearchControllerApi(accessToken)
				.crudTransfersFindBySemanticKey(0L, "iuv", "iud", 1),
			Object.class,
			transferApisHolder::unload);
	}

	@Test
	void whenGetTransferApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
		assertAuthenticationShouldBeSetInThreadSafeMode(
			accessToken -> transferApisHolder.getTransferApi(accessToken)
				.notifyReportedTransferId(0L),
			Object.class,
			transferApisHolder::unload);
	}
}