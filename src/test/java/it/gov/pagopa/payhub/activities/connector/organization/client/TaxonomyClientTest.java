package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.TaxonomyApi;
import it.gov.pagopa.pu.organization.client.generated.TaxonomySearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelTaxonomy;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelTaxonomyEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
class TaxonomyClientTest {

	@Mock
	private OrganizationApisHolder organizationApisHolderMock;
	@Mock
	private TaxonomySearchControllerApi taxonomySearchControllerApiMock;

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



	@Test
	void whenFindTaxonomiesThenInvokeWithAccessToken() {
		// Given
		String organizationType = "00";
		String macroAreaCode = "11";
		String serviceTypeCode = "222";
		String collectionReason = "33";
		String accessToken = "ACCESSTOKEN";

		PagedModelTaxonomy pagedModelTaxonomy = PagedModelTaxonomy.builder()
				.embedded(PagedModelTaxonomyEmbedded.builder()
						.taxonomies(List.of(buildTaxonomy()))
						.build())
				.build();

		Mockito.when(organizationApisHolderMock.getTaxonomySearchControllerApi(accessToken))
				.thenReturn(taxonomySearchControllerApiMock);
		Mockito.when(taxonomySearchControllerApiMock.crudTaxonomiesFindTaxonomies(organizationType, macroAreaCode,
						serviceTypeCode, collectionReason, 0, 1, null))
				.thenReturn(pagedModelTaxonomy);

		// When
		PagedModelTaxonomy result = client.findTaxonomies(organizationType, macroAreaCode,
				serviceTypeCode, collectionReason, 0, 1, null, accessToken);

		// Then
		Assertions.assertSame(pagedModelTaxonomy, result);
	}

}