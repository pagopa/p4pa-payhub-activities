package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.util.HttpTestUtils;
import it.gov.pagopa.payhub.activities.util.HttpUtils;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactDownloadMetadataDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class FetchSendLegalFactActivityImplTest {

	@Mock
	private SendService sendServiceMock;
	@Mock
	private SendNotificationService sendNotificationServiceMock;

	@InjectMocks
	private FetchSendLegalFactActivityImpl fetchSendLegalFactActivity;

	@AfterEach
	void verifyNoMoreInteractions() {
		Mockito.verifyNoMoreInteractions(
			sendServiceMock,
			sendNotificationServiceMock
		);
	}

	@Test
	void givenNullLegalFactDownloadMetadataDTOWhenDownloadAndCacheSendLegalFactThenReturn() throws IOException {
		//GIVEN
		String sendNotificationId = "sendNotificationId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		Mockito.when(sendServiceMock.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId))
			.thenReturn(null);

		//WHEN
		fetchSendLegalFactActivity.downloadAndCacheSendLegalFact(
			sendNotificationId,
			category,
			legalFactId
		);

		//THEN
		Mockito.verify(sendServiceMock).retrieveLegalFactDownloadMetadata(
			sendNotificationId,
			legalFactId
		);
		Mockito.verify(sendNotificationServiceMock, Mockito.times(0)).uploadSendLegalFact(
				sendNotificationId,
				category,
				legalFactId,
				null
		);
	}

	@Test
	void givenNullPreSignedUrlWhenDownloadAndCacheSendLegalFactThenReturn() throws IOException {
		//GIVEN
		String sendNotificationId = "sendNotificationId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		LegalFactDownloadMetadataDTO legalFactDownloadMetadataDTO = new LegalFactDownloadMetadataDTO();

		Mockito.when(sendServiceMock.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId))
				.thenReturn(legalFactDownloadMetadataDTO);

		//WHEN
		fetchSendLegalFactActivity.downloadAndCacheSendLegalFact(
				sendNotificationId,
				category,
				legalFactId
		);

		//THEN
		Mockito.verify(sendServiceMock).retrieveLegalFactDownloadMetadata(
				sendNotificationId,
				legalFactId
		);
		Mockito.verify(sendNotificationServiceMock, Mockito.times(0)).uploadSendLegalFact(
				sendNotificationId,
				category,
				legalFactId,
				null
		);
	}

	@Test
	void givenCorrectPreSignedUrlWhenDownloadAndCacheSendLegalFactThenReturnOk() throws IOException {
		//GIVEN
		String sendNotificationId = "sendNotificationId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		LegalFactDownloadMetadataDTO legalFactDownloadMetadataDTO = new LegalFactDownloadMetadataDTO();
		legalFactDownloadMetadataDTO.setUrl("http://localhost:8080");

		Path pathMock = Mockito.mock(Path.class);
		File fileMock = Mockito.mock(File.class);
		Mockito.when(pathMock.toFile()).thenReturn(fileMock);

		HttpResponse<Path> httpResponse = HttpTestUtils.basicHttpOkResponse(pathMock, null, null);

		try (MockedStatic<HttpUtils> utilities = Mockito.mockStatic(HttpUtils.class)) {
			utilities.when(() -> HttpUtils.fetchFromPreSignedUrl(Mockito.isA(URI.class), Mockito.isA(Path.class)))
					.thenReturn(httpResponse);

			Mockito.when(sendServiceMock.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId))
				.thenReturn(legalFactDownloadMetadataDTO);
			Mockito.doNothing().when(sendNotificationServiceMock)
				.uploadSendLegalFact(sendNotificationId, category, legalFactId, fileMock);

			//WHEN
			fetchSendLegalFactActivity.downloadAndCacheSendLegalFact(
				sendNotificationId,
				category,
				legalFactId
			);
		}

		//THEN
		Mockito.verify(sendServiceMock).retrieveLegalFactDownloadMetadata(
			sendNotificationId,
			legalFactId
		);
		Mockito.verify(sendNotificationServiceMock).uploadSendLegalFact(
			sendNotificationId,
			category,
			legalFactId,
			pathMock.toFile()
		);
	}

}