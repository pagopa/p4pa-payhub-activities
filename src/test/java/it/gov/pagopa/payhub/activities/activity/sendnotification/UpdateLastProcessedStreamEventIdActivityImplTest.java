package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateLastProcessedStreamEventIdActivityImplTest {

	@Mock
	private SendNotificationService sendNotificationServiceMock;

	@InjectMocks
	private UpdateLastProcessedStreamEventIdActivityImpl activity;

	@AfterEach
	void verifyNoMoreInteractions() {
		Mockito.verifyNoMoreInteractions(
			sendNotificationServiceMock
		);
	}

	@Test
	void updateLastProcessedStreamEventId() {
		//GIVEN
		String streamId = "streamId";
		String lastEventId = "lastEventId";

		Mockito.doNothing()
				.when(sendNotificationServiceMock)
				.updateLastProcessedStreamEventId(
					streamId,
					lastEventId
				);

		//WHEN
		activity.updateLastProcessedStreamEventId(streamId, lastEventId);

		//THEN
		Mockito.verify(sendNotificationServiceMock)
				.updateLastProcessedStreamEventId(
						streamId,
						lastEventId
				);
	}

}