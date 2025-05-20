package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelBroker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerClientTest {
	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 2_000;

	@Mock
	private OrganizationApisHolder organizationApisHolderMock;

	private BrokerClient client;

	@BeforeEach
	void setUp() {
		client = new BrokerClient(organizationApisHolderMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(organizationApisHolderMock);
	}

	@Test
	void testFetchAll() {
		// Given
		String accessToken = "accessToken";
		PagedModelBroker expectedResponse = mock(PagedModelBroker.class);
		BrokerEntityControllerApi mockApi = mock(BrokerEntityControllerApi.class);
		when(organizationApisHolderMock.getBrokerEntityControllerApi(accessToken)).thenReturn(mockApi);
		when(mockApi.crudGetBrokers(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, null)).thenReturn(expectedResponse);

		// When
		PagedModelBroker pagedModelBroker = client.fetchAll(accessToken);
		// Then
		assertEquals(expectedResponse, pagedModelBroker);
		verify(organizationApisHolderMock.getBrokerEntityControllerApi(accessToken), times(1))
			.crudGetBrokers(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, null);
	}

	@Test
	void testGetByFiscalCode() {
		// Given
		String fiscalCode = "ABCDEF12G34H567I";
		String accessToken = "accessToken";
		Broker expectedBroker = mock(Broker.class);
		var brokerSearchControllerApi = mock(it.gov.pagopa.pu.organization.client.generated.BrokerSearchControllerApi.class);

		when(organizationApisHolderMock.getBrokerSearchControllerApi(accessToken)).thenReturn(brokerSearchControllerApi);
		when(brokerSearchControllerApi.crudBrokersFindByBrokeredOrgFiscalCode(fiscalCode)).thenReturn(expectedBroker);

		// When
		Broker result = client.getByFiscalCode(fiscalCode, accessToken);

		// Then
		assertEquals(expectedBroker, result);
		verify(organizationApisHolderMock).getBrokerSearchControllerApi(accessToken);
		verify(brokerSearchControllerApi).crudBrokersFindByBrokeredOrgFiscalCode(fiscalCode);
	}
}