package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.ProgressResponseElementV25DTO;
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
class GetSendNotificationEventFromStreamActivityTest {

	@Mock
	private SendNotificationService serviceMock;

	private GetSendNotificationEventFromStreamActivity activity;

	@BeforeEach
	void init(){
		this.activity = new GetSendNotificationEventFromStreamActivityImpl(serviceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void testFetchSendNotificationEventFromStream() {
		// Given
		Long organizationId = 1L;
		String streamId = "streamId";
		String lastEventId = "lastEventId";

		List<ProgressResponseElementV25DTO> expectedResult = List.of(new ProgressResponseElementV25DTO());

		Mockito.when(serviceMock.readSendStreamEvents(organizationId, streamId, lastEventId))
				.thenReturn(expectedResult);

		// When
		List<ProgressResponseElementV25DTO> actualResult = activity.fetchSendNotificationEventFromStream(organizationId, streamId, lastEventId);

		// Then
		Assertions.assertSame(expectedResult, actualResult);
	}
}