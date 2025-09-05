package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.TaxonomyApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxonomyClientTest {

	@Mock
	private OrganizationApisHolder organizationApisHolderMock;

	private TaxonomyClient client;

	@BeforeEach
	void setUp() {
		client = new TaxonomyClient(organizationApisHolderMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(organizationApisHolderMock);
	}

	@Test
	void fetchTaxonomiesReturnsExpectedValue() {
		// Given
		String accessToken = "accessToken";
		TaxonomyApi mockApi = mock(TaxonomyApi.class);
		when(organizationApisHolderMock.getTaxonomyApi(accessToken)).thenReturn(mockApi);
		when(mockApi.syncTaxonomies()).thenReturn(5);

		// When
		Integer result = client.fetchTaxonomies(accessToken);

		// Then
		assertEquals(5, result);
		verify(organizationApisHolderMock).getTaxonomyApi(accessToken);
		verify(mockApi).syncTaxonomies();
	}
}