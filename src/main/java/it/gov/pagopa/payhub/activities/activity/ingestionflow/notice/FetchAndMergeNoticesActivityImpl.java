package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.files.ZipFileService;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Lazy
@Slf4j
@Service
public class FetchAndMergeNoticesActivityImpl implements FetchAndMergeNoticesActivity {
    private final PrintPaymentNoticeService printPaymentNoticeService;
    private final IngestionFlowFileService ingestionFlowFileService;
    private final FoldersPathsConfig foldersPathsConfig;
    private final ZipFileService zipFileService;
    private final FileArchiverService fileArchiverService;
    private final RestTemplate noRedirectRestTemplate;

    public FetchAndMergeNoticesActivityImpl(
            PrintPaymentNoticeService printPaymentNoticeService,
            IngestionFlowFileService ingestionFlowFileService,
            FoldersPathsConfig foldersPathsConfig,
            ZipFileService zipFileService,
            FileArchiverService fileArchiverService
    ) {
        this.printPaymentNoticeService = printPaymentNoticeService;
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.foldersPathsConfig = foldersPathsConfig;
        this.zipFileService = zipFileService;
        this.fileArchiverService = fileArchiverService;
        this.noRedirectRestTemplate = createNoRedirectRestTemplate();
    }

    @Override
    public Integer fetchAndMergeNotices(Long ingestionFlowFileId) {
        IngestionFlowFile ingestionFlowFile = ingestionFlowFileService
                .findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("[FILE_NOT_FOUND] IngestionFlowFile with id " + ingestionFlowFileId + " not found"));

        Long organizationId = ingestionFlowFile.getOrganizationId();

        List<String> signedUrls = retrieveSignedUrls(ingestionFlowFile, organizationId);
        if (signedUrls.isEmpty()) {
            return 0;
        }

        Path tmpDir = FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getTmp(), organizationId)
                .resolve(ingestionFlowFile.getFilePathName())
                .resolve("merge_notices_" + ingestionFlowFileId);

        try {
            Files.createDirectories(tmpDir);

            List<Path> allExtractedNotices = downloadAndExtractAllNotices(organizationId, ingestionFlowFileId, signedUrls, tmpDir);
            if (allExtractedNotices.isEmpty()) {
                return 0;
            }

            archiveMergedNotices(ingestionFlowFile, allExtractedNotices, tmpDir);

            return allExtractedNotices.size();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot process and merge notices in working directory: " + tmpDir, e);
        }
    }

    private List<String> retrieveSignedUrls(IngestionFlowFile file, Long organizationId) {
        String pdfGeneratedId = file.getPdfGeneratedId();
        if (pdfGeneratedId == null) {
            log.info("No folderId found for ingestionFlowFileId: {}", file.getIngestionFlowFileId());
            return Collections.emptyList();
        }

        List<String> urls = new ArrayList<>();
        String[] folderIds = pdfGeneratedId.split(",");

        for (String folderId : folderIds) {
            try {
                SignedUrlResultDTO dto = printPaymentNoticeService.getSignedUrl(organizationId, folderId.trim());
                urls.add(dto.getSignedUrl());
            } catch (HttpClientErrorException.NotFound e) {
                return Collections.emptyList();
            }
        }

        return urls;
    }

    private List<Path> downloadAndExtractAllNotices(Long organizationId, Long ingestionFlowFileId, List<String> signedUrls, Path tmpDir) {
        return IntStream.range(0, signedUrls.size())
                .mapToObj(i -> downloadAndExtractSingleNotice(organizationId, ingestionFlowFileId, signedUrls.get(i), tmpDir, i))
                .flatMap(List::stream)
                .toList();
    }

    private List<Path> downloadAndExtractSingleNotice(Long organizationId, Long ingestionFlowFileId, String url, Path tmpDir, int index) {
        byte[] downloadedBytes = downloadNoticeArchive(organizationId, ingestionFlowFileId, url);

        Path downloadedZipPath = tmpDir.resolve("downloaded_" + index + ".zip");
        Path extractDirPath = tmpDir.resolve("extracted_" + index);

        try {
            Files.write(downloadedZipPath, downloadedBytes);
            return zipFileService.unzip(downloadedZipPath, extractDirPath);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing or extracting zip: " + downloadedZipPath, e);
        }
    }

    private byte[] downloadNoticeArchive(Long organizationId, Long ingestionFlowFileId, String signedUrl) {
        try {
            URI uri = URI.create(signedUrl);

            ResponseEntity<byte[]> response = noRedirectRestTemplate.getForEntity(uri, byte[].class);
            if (response.getBody() == null) {
                throw new IllegalStateException(String.format("[INVALID_FILE_EMPTY] Downloaded file from signed url: %s for ingestionFlowFileId: %s is empty", signedUrl, ingestionFlowFileId));
            }

            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error downloading archive for organizationId {} and fileId {}", organizationId, ingestionFlowFileId, e);
            throw e;
        }
    }

    private void archiveMergedNotices(IngestionFlowFile file, List<Path> allExtractedFiles, Path tmpDir) throws IOException {
        String mergedFileName = file.getFileName().replace(".zip", "_notice.zip");
        Path tmpZipFilePath = tmpDir.resolve(mergedFileName);

        Path sharedTargetPath = FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), file.getOrganizationId())
                .resolve(file.getFilePathName())
                .resolve(foldersPathsConfig.getProcessTargetSubFolders().getArchive());

        fileArchiverService.compressAndArchive(allExtractedFiles, tmpZipFilePath, sharedTargetPath);
    }

    private RestTemplate createNoRedirectRestTemplate() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableRedirectHandling()
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
