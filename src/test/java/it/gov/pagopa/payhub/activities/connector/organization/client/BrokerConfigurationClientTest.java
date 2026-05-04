package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.BrokerConfigurationApi;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerConfigurationClientTest {

	@Mock
	private OrganizationApisHolder organizationApisHolderMock;

	private BrokerConfigurationClient client;

	@BeforeEach
	void setUp() {
		client = new BrokerConfigurationClient(organizationApisHolderMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(organizationApisHolderMock);
	}

	@Test
	void whenGetBrokerEmailServerConfigThenOk() {
		String accessToken = "accessToken";
		Long brokerId = 1L;
		EmailServerConfigDTO expectedResponse = mock(EmailServerConfigDTO.class);
		BrokerConfigurationApi mockApi = mock(BrokerConfigurationApi.class);
		when(organizationApisHolderMock.getBrokerConfigurationApi(accessToken))
				.thenReturn(mockApi);
		when(mockApi.getBrokerEmailServerConfig(brokerId))
				.thenReturn(expectedResponse);

		EmailServerConfigDTO response = client.getBrokerEmailServerConfig(brokerId, accessToken);

		assertEquals(expectedResponse, response);
	}

	@Test
	void givenNoEmailServerConfigDTOWhenGetBrokerEmailServerConfigThenNull() {
		String accessToken = "accessToken";
		Long brokerId = 1L;
		BrokerConfigurationApi mockApi = mock(BrokerConfigurationApi.class);
		when(organizationApisHolderMock.getBrokerConfigurationApi(accessToken))
				.thenReturn(mockApi);
		when(mockApi.getBrokerEmailServerConfig(brokerId))
				.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

		EmailServerConfigDTO response = client.getBrokerEmailServerConfig(brokerId, accessToken);

		assertNull(response);
	}
}