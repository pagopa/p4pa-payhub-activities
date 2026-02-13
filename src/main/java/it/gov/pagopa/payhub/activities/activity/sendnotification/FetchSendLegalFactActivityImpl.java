package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.util.HttpUtils;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactCategoryDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.LegalFactDownloadMetadataDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@Lazy
public class FetchSendLegalFactActivityImpl implements FetchSendLegalFactActivity {

	private final SendService sendService;
	private final SendNotificationService sendNotificationService;

	private final Path tempDirectoryPath;

	public FetchSendLegalFactActivityImpl(
			@Value("${folders.tmp}") String tempFolder,
			SendService sendService,
			SendNotificationService sendNotificationService) {
		this.tempDirectoryPath = Path.of(tempFolder);
		this.sendService = sendService;
		this.sendNotificationService = sendNotificationService;
	}

	@Override
	public void downloadAndCacheSendLegalFact(String notificationRequestId, LegalFactCategoryDTO category, String legalFactId) throws IOException {
		SendNotificationDTO sendNotificationDTO = sendService.retrieveNotificationByNotificationRequestId(notificationRequestId);
		if(sendNotificationDTO == null) {
			String formattedErrorMessage = "Error in fetching SEND notification by notificationRequestId %s".formatted(notificationRequestId);
			log.error(formattedErrorMessage);
			return;
		}
		String sendNotificationId = sendNotificationDTO.getSendNotificationId();
		LegalFactDownloadMetadataDTO legalFactDownloadMetadataDTO =
				sendService.retrieveLegalFactDownloadMetadata(sendNotificationId, legalFactId);
		if(legalFactDownloadMetadataDTO == null || legalFactDownloadMetadataDTO.getUrl() == null) {
			String formattedErrorMessage = "Error in fetching SEND LegalFact pre-signed URL for sendNotificationId %s, category %s, legalFactId %s"
					.formatted(sendNotificationId, category.getValue(), legalFactId);
			log.error(formattedErrorMessage);
			return;
		}
		String preSignedUrl = legalFactDownloadMetadataDTO.getUrl();
		Path tmpTargetDirPath = tempDirectoryPath
				.resolve(String.valueOf(sendNotificationDTO.getOrganizationId()))
				.resolve("send-legal-fact");

		if (!Files.exists(tmpTargetDirPath)) {
			Files.createDirectories(tmpTargetDirPath);
		}

		File tempFile = File.createTempFile("sendLegalFactDownload-%s-".formatted(legalFactId), ".tmp", tmpTargetDirPath.toFile());
		try {
			HttpUtils.downloadFromPreSignedUrl(URI.create(preSignedUrl), tempFile.toPath());
			sendNotificationService.uploadSendLegalFact(sendNotificationId, category, legalFactId, tempFile);
		} finally {
			Files.deleteIfExists(tempFile.toPath());
		}
	}
}
