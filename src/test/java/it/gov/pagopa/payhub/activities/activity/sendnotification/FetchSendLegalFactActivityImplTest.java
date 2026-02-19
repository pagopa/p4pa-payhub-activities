package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

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

	@Test
	void givenBadRequestWhenDownloadAndCacheSendLegalFactThenThrowNotRetryableActivityException() {
		//GIVEN
		String notificationRequestId = "notificationRequestId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		Mockito.doThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "BadRequest", null, null, null))
				.when(sendServiceMock)
				.downloadAndArchiveSendLegalFact(
						notificationRequestId,
						category,
						legalFactId
				);

		//WHEN
		NotRetryableActivityException notRetryableActivityException = Assertions.assertThrows(
			NotRetryableActivityException.class, () ->
				fetchSendLegalFactActivity.downloadAndArchiveSendLegalFact(
					notificationRequestId,
					category,
					legalFactId
				)
		);

		//THEN
		Assertions.assertNotNull(notRetryableActivityException);
		Assertions.assertEquals(
				"Bad request in downloadAndArchiveSendLegalFact for notificationRequestId %s, legal fact category %s and id %s".formatted(notificationRequestId, LegalFactCategoryDTO.ANALOG_DELIVERY, legalFactId),
			notRetryableActivityException.getMessage()
		);
		Mockito.verify(sendServiceMock)
				.downloadAndArchiveSendLegalFact(
						notificationRequestId,
						category,
						legalFactId
				);
	}

}