package it.gov.pagopa.payhub.activities.activity.organization;

import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
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
class BrokersRetrieverActivityTest {
	@Mock
	private BrokerService brokerServiceMock;

	private BrokersRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new BrokersRetrieverActivityImpl(brokerServiceMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(brokerServiceMock);
	}

	@Test
	void testFetchAllBrokers() {
		// Given
		List<Broker> expected = List.of(new Broker());

		when(brokerServiceMock.fetchAll()).thenReturn(expected);
		// When
		List<Broker> result = activity.fetchAllBrokers();
		// Then
		assertEquals(expected, result);
	}
}