package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeInvalidValueException;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendStreamSkippedEventException;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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
		verify(sendServiceMock)
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

		doThrow(new RestInvokeInvalidValueException("APPNAME", HttpStatus.BAD_REQUEST, "ERROR", "ERRORCODE", "ERRORMESSAGE", null))
				.when(sendServiceMock)
				.downloadAndArchiveSendLegalFact(
						notificationRequestId,
						category,
						legalFactId
				);

		//WHEN
		SendStreamSkippedEventException sendStreamSkippedEventException = Assertions.assertThrows(
				SendStreamSkippedEventException.class, () ->
				fetchSendLegalFactActivity.downloadAndArchiveSendLegalFact(
					notificationRequestId,
					category,
					legalFactId
				)
		);

		//THEN
		Assertions.assertNotNull(sendStreamSkippedEventException);
		String causeErrorMessage = "Bad request in downloadAndArchiveSendLegalFact for notificationRequestId %s, legal fact category %s and id %s: error message ERRORMESSAGE".formatted(notificationRequestId, LegalFactCategoryDTO.ANALOG_DELIVERY, legalFactId);
		Assertions.assertEquals(
			"Skipped an error during execution of activity %s: %s".formatted(FetchSendLegalFactActivity.class.getSimpleName(), causeErrorMessage),
			sendStreamSkippedEventException.getMessage()
		);
		verify(sendServiceMock)
				.downloadAndArchiveSendLegalFact(
						notificationRequestId,
						category,
						legalFactId
				);
	}

}