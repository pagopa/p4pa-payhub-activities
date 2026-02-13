package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.util.HttpUtils;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactDownloadMetadataDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
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
	void givenNullSendNotificationDTOWhenDownloadAndCacheSendLegalFactThenReturn() throws IOException {
		//GIVEN
		String notificationRequestId = "notificationRequestId";
		String sendNotificationId = "sendNotificationId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
		sendNotificationDTO.setSendNotificationId(sendNotificationId);

		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId))
				.thenReturn(null);

		//WHEN
		fetchSendLegalFactActivity.downloadAndCacheSendLegalFact(
				notificationRequestId,
				category,
				legalFactId
		);

		//THEN
		Mockito.verify(sendServiceMock, Mockito.times(0)).retrieveLegalFactDownloadMetadata(
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
	void givenNullLegalFactDownloadMetadataDTOWhenDownloadAndCacheSendLegalFactThenReturn() throws IOException {
		//GIVEN
		String notificationRequestId = "notificationRequestId";
		String sendNotificationId = "sendNotificationId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
		sendNotificationDTO.setSendNotificationId(sendNotificationId);

		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId))
				.thenReturn(sendNotificationDTO);
		Mockito.when(sendServiceMock.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId))
			.thenReturn(null);

		//WHEN
		fetchSendLegalFactActivity.downloadAndCacheSendLegalFact(
			notificationRequestId,
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
		String notificationRequestId = "notificationRequestId";
		String sendNotificationId = "sendNotificationId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		LegalFactDownloadMetadataDTO legalFactDownloadMetadataDTO = new LegalFactDownloadMetadataDTO();

		SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
		sendNotificationDTO.setSendNotificationId(sendNotificationId);

		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId))
				.thenReturn(sendNotificationDTO);
		Mockito.when(sendServiceMock.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId))
				.thenReturn(legalFactDownloadMetadataDTO);

		//WHEN
		fetchSendLegalFactActivity.downloadAndCacheSendLegalFact(
				notificationRequestId,
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
		String notificationRequestId = "notificationRequestId";
		String sendNotificationId = "sendNotificationId";
		LegalFactCategoryDTO category = LegalFactCategoryDTO.ANALOG_DELIVERY;
		String legalFactId = "sendLegalFact.pdf";

		LegalFactDownloadMetadataDTO legalFactDownloadMetadataDTO = new LegalFactDownloadMetadataDTO();
		legalFactDownloadMetadataDTO.setUrl("http://localhost:8080");

		Path tmpDirectoryPath = Path.of(FetchSendLegalFactActivityImpl.TMP_DIRECTORY_PATH_STRING);
		if(!tmpDirectoryPath.toFile().exists()) {
			Files.createDirectories(tmpDirectoryPath);
		}

		try (MockedStatic<HttpUtils> utilities = Mockito.mockStatic(HttpUtils.class);
			MockedStatic<File> fileMocked = Mockito.mockStatic(File.class)) {
			utilities.when(() -> HttpUtils.downloadFromPreSignedUrl(Mockito.isA(URI.class), Mockito.isA(Path.class)))
				.thenAnswer(i -> null);
			fileMocked.when(() -> File.createTempFile(
					Mockito.eq("sendLegalFactDownload-%s-".formatted(legalFactId)),
					Mockito.eq(".tmp"),
					Mockito.eq(Path.of(FetchSendLegalFactActivityImpl.TMP_DIRECTORY_PATH_STRING).toFile()))
				).thenCallRealMethod();

			ArgumentCaptor<File> fileArgumentCaptor = ArgumentCaptor.forClass(File.class);

			SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
			sendNotificationDTO.setSendNotificationId(sendNotificationId);

			Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId))
					.thenReturn(sendNotificationDTO);
			Mockito.when(sendServiceMock.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId))
				.thenReturn(legalFactDownloadMetadataDTO);

			//WHEN
			fetchSendLegalFactActivity.downloadAndCacheSendLegalFact(
				notificationRequestId,
				category,
				legalFactId
			);

			//THEN
			fileMocked.verify(
					() -> File.createTempFile(
							Mockito.eq("sendLegalFactDownload-%s-".formatted(legalFactId)),
							Mockito.eq(".tmp"),
							fileArgumentCaptor.capture()
					)
			);

			Mockito.verify(sendServiceMock).retrieveLegalFactDownloadMetadata(
				sendNotificationId,
				legalFactId
			);
			Mockito.verify(sendNotificationServiceMock).uploadSendLegalFact(
				Mockito.eq(sendNotificationId),
				Mockito.eq(category),
				Mockito.eq(legalFactId),
				fileArgumentCaptor.capture()
			);
		}
	}

}