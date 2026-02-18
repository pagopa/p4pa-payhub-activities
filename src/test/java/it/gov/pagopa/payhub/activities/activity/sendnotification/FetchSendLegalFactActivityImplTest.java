package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FetchSendLegalFactActivityImplTest {

	@Mock
	private SendService sendServiceMock;

	@InjectMocks
	private FetchSendLegalFactActivityImpl fetchSendLegalFactActivity;

	@AfterEach
	void verifyNoMoreInteractions() {
		Mockito.verifyNoMoreInteractions(
			sendServiceMock
		);
	}

	@Test
	void givenNullSendNotificationDTOWhenDownloadAndCacheSendLegalFactThenReturn() {
		//GIVEN
		String notificationRequestId = "notificationRequestId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		Mockito.doNothing()
			.when(sendServiceMock)
			.downloadAndArchiveSendLegalFact(
				notificationRequestId,
				category,
				legalFactId
			);

		//WHEN
		fetchSendLegalFactActivity.downloadAndArchiveSendLegalFact(
			notificationRequestId,
			category,
			legalFactId
		);

		//THEN
		Mockito.verify(sendServiceMock)
			.downloadAndArchiveSendLegalFact(
				notificationRequestId,
				category,
				legalFactId
			);
	}

}