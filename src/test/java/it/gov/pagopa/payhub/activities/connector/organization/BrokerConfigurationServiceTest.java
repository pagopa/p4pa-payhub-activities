package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.BrokerConfigurationClient;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerConfigurationServiceTest {
	@Mock
	private AuthnService authnServiceMock;
	@Mock
	private BrokerConfigurationClient brokerConfigurationClientMock;

	private BrokerConfigurationService service;

	@BeforeEach
	void setUp() { service = new BrokerConfigurationServiceImpl(authnServiceMock, brokerConfigurationClientMock); }

	@AfterEach
	void tearDown() { verifyNoMoreInteractions(authnServiceMock, brokerConfigurationClientMock);	}

	@Test
	void whenGetBrokerEmailServerConfigThenOk() {
		String accessToken = "accessToken";
		Long brokerId = 1L;
		EmailServerConfigDTO expectedResponse = new EmailServerConfigDTO();
		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(brokerConfigurationClientMock.getBrokerEmailServerConfig(brokerId, accessToken)).thenReturn(expectedResponse);

		EmailServerConfigDTO result = service.getBrokerEmailServerConfig(brokerId);

		assertEquals(expectedResponse, result);
	}
}