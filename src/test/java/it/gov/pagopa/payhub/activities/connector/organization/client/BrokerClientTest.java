package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelBroker;
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
class BrokerClientTest {
	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 20;
	private static final List<String> DEFAULT_SORT = List.of("asc");

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
		when(mockApi.crudGetBrokers(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, DEFAULT_SORT)).thenReturn(expectedResponse);

		// When
		PagedModelBroker pagedModelBroker = client.fetchAll(accessToken);
		// Then
		assertEquals(expectedResponse, pagedModelBroker);
		verify(organizationApisHolderMock.getBrokerEntityControllerApi(accessToken), times(1))
			.crudGetBrokers(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
	}
}