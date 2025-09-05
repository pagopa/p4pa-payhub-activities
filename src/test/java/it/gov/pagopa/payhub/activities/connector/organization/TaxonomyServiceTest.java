package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.TaxonomyClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxonomyServiceTest {
	@Mock
	private AuthnService authnServiceMock;
	@Mock
	private TaxonomyClient taxonomyClientMock;

	private TaxonomyService service;

	@BeforeEach
	void setUp() { service = new TaxonomyServiceImpl(authnServiceMock, taxonomyClientMock); }

	@AfterEach
	void tearDown() { verifyNoMoreInteractions(authnServiceMock, taxonomyClientMock);	}

	@Test
	void testFetchTaxonomies() {
		// Given
		String accessToken = "accessToken";
		Integer expectedResponse = Integer.valueOf(5);
		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(taxonomyClientMock.fetchTaxonomies(accessToken)).thenReturn(expectedResponse);
		// When
		Integer result = service.syncTaxonomies();

		// Then
		assertEquals(expectedResponse, result);
		verify(taxonomyClientMock, times(1)).fetchTaxonomies(accessToken);
	}
}