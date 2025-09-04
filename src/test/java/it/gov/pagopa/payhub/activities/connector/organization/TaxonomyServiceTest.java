package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.TaxonomyClient;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelTaxonomy;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelTaxonomyEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.TaxonomyFaker.buildTaxonomy;
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


	@Test
	void givenOneTaxonomyWhenFindTaxonomiesThenSuccess() {
		// Given
		String accessToken = "accessToken";
		String organizationType = "00";
		String macroAreaCode = "11";
		String serviceTypeCode = "222";
		String collectionReason = "33";

		PagedModelTaxonomy pagedModelTaxonomy = PagedModelTaxonomy.builder()
				.embedded(PagedModelTaxonomyEmbedded.builder()
						.taxonomies(List.of(buildTaxonomy()))
						.build())
				.build();

		Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		Mockito.when(taxonomyClientMock.findTaxonomies(
				Mockito.eq(organizationType),
				Mockito.eq(macroAreaCode),
				Mockito.eq(serviceTypeCode),
				Mockito.eq(collectionReason),
				Mockito.eq(0),
				Mockito.eq(10),
				Mockito.isNull(),
				Mockito.eq(accessToken)
		)).thenReturn(pagedModelTaxonomy);

		// When
		PagedModelTaxonomy result = service.getTaxonomies(organizationType, macroAreaCode, serviceTypeCode, collectionReason, 0, 10, null);

		// Then
		assertEquals(result, pagedModelTaxonomy);
	}

}