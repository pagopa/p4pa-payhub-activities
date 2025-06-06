package it.gov.pagopa.payhub.activities.activity.organization;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationBrokeredActiveRetrieverActivityImplTest {
	@Mock
	private OrganizationService organizationServiceMock;

	private OrganizationBrokeredActiveRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationBrokeredActiveRetrieverActivityImpl(organizationServiceMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(organizationServiceMock);
	}

	@Test
	void testRetrieveBrokeredOrganizations() {
		// Given
		Long brokerId = 1L;
		List<Organization> expectedOrganizations = List.of(new Organization());

		when(organizationServiceMock.getActiveOrganizationsByBrokerId(brokerId)).thenReturn(expectedOrganizations);

		// When
		List<Organization> result = activity.retrieveBrokeredOrganizations(brokerId);

		// Then
		assertEquals(expectedOrganizations, result);
	}
}