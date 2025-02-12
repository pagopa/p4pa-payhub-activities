package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.BrokerClient;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelBroker;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelBrokerEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerServiceTest {
	@Mock
	private AuthnService authnServiceMock;
	@Mock
	private BrokerClient brokerClientMock;

	private BrokerService service;

	@BeforeEach
	void setUp() { service = new BrokerServiceImpl(authnServiceMock, brokerClientMock); }

	@AfterEach
	void tearDown() { verifyNoMoreInteractions(authnServiceMock, brokerClientMock);	}

	@Test
	void testFetchAll() {
		// Given
		String accessToken = "accessToken";
		PagedModelBrokerEmbedded embedded = mock(PagedModelBrokerEmbedded.class);
		PagedModelBroker expectedResponse = new PagedModelBroker(embedded, null, null);
		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(brokerClientMock.fetchAll(accessToken)).thenReturn(expectedResponse);
		// When
		List<Broker> result = service.fetchAll();

		// Then
		assertEquals(embedded.getBrokers(), result);
		verify(brokerClientMock, times(1)).fetchAll(accessToken);
	}
}