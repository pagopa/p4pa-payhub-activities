package it.gov.pagopa.payhub.activities.activity.sendnotification.stream;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.ProgressResponseElementV28DTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GetSendNotificationEventsFromStreamActivityTest {

	@Mock
	private SendNotificationService serviceMock;

	private GetSendNotificationEventsFromStreamActivity activity;

	@BeforeEach
	void init(){
		this.activity = new GetSendNotificationEventsFromStreamActivityImpl(serviceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void testFetchSendNotificationEventsFromStream() {
		// Given
		Long organizationId = 1L;
		String streamId = "streamId";

		List<ProgressResponseElementV28DTO> expectedResult = List.of(new ProgressResponseElementV28DTO());

		Mockito.when(serviceMock.readSendStreamEvents(organizationId, streamId))
				.thenReturn(expectedResult);

		// When
		List<ProgressResponseElementV28DTO> actualResult = activity.fetchSendNotificationEventsFromStream(organizationId, streamId);

		// Then
		Assertions.assertSame(expectedResult, actualResult);
	}
}