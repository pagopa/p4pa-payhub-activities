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
class OrganizationBrokeredRetrieverActivityImplTest {
	@Mock
	private OrganizationService organizationServiceMock;

	private OrganizationBrokeredRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationBrokeredRetrieverActivityImpl(organizationServiceMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(organizationServiceMock);
	}

	@Test
	void testRetrieve() {
		// Given
		Long brokerId = 1L;
		List<Organization> expectedOrganizations = List.of(new Organization());

		when(organizationServiceMock.getOrganizationsByBrokerId(brokerId)).thenReturn(expectedOrganizations);

		// When
		List<Organization> result = activity.retrieve(brokerId);

		// Then
		assertEquals(expectedOrganizations, result);
	}
}