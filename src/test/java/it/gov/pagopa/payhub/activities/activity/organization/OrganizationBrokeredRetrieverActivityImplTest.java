package it.gov.pagopa.payhub.activities.activity.organization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class OrganizationBrokeredRetrieverActivityImplTest {
	private OrganizationBrokeredRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationBrokeredRetrieverActivityImpl();
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions();
	}

	@Test
	void testRetrieve() {
		// Given
		Long brokerId = 1L;

		// When
		var result = activity.retrieve(brokerId);

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}