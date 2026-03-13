package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.connector.signedurl.SignedUrlService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.files.ZipFileService;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
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
    private final SignedUrlService signedUrlService;

    public FetchAndMergeNoticesActivityImpl(
            PrintPaymentNoticeService printPaymentNoticeService,
            IngestionFlowFileService ingestionFlowFileService,
            FoldersPathsConfig foldersPathsConfig,
            ZipFileService zipFileService,
            FileArchiverService fileArchiverService,
            SignedUrlService signedUrlService
    ) {
        this.printPaymentNoticeService = printPaymentNoticeService;
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.foldersPathsConfig = foldersPathsConfig;
        this.zipFileService = zipFileService;
        this.fileArchiverService = fileArchiverService;
        this.signedUrlService = signedUrlService;
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

            List<Path> allExtractedNotices = downloadAndExtractAllNotices(signedUrls, tmpDir);
            if (allExtractedNotices.isEmpty()) {
                return 0;
            }

            archiveMergedNotices(ingestionFlowFile, allExtractedNotices, tmpDir);

            return allExtractedNotices.size();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot process and merge notices in working directory: " + tmpDir, e);
        } finally {
            cleanupTmpDir(tmpDir);
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
            SignedUrlResultDTO dto = printPaymentNoticeService.getSignedUrl(organizationId, folderId.trim());
            if (dto == null) {
                return Collections.emptyList();
            }
            urls.add(dto.getSignedUrl());
        }

        return urls;
    }

    private List<Path> downloadAndExtractAllNotices(List<String> signedUrls, Path tmpDir) {
        return IntStream.range(0, signedUrls.size())
                .mapToObj(i -> downloadAndExtractSingleNotice(signedUrls.get(i), tmpDir, i))
                .flatMap(List::stream)
                .toList();
    }

    private List<Path> downloadAndExtractSingleNotice(String url, Path tmpDir, int index) {
        byte[] downloadedBytes = signedUrlService.downloadFileFromSignedUrl(url);

        Path downloadedZipPath = tmpDir.resolve("downloaded_" + index + ".zip");
        Path extractDirPath = tmpDir.resolve("extracted_" + index);

        try {
            Files.write(downloadedZipPath, downloadedBytes);
            return zipFileService.unzip(downloadedZipPath, extractDirPath);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing or extracting zip: " + downloadedZipPath, e);
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

    private void cleanupTmpDir(Path tmpDir) {
        try {
            FileSystemUtils.deleteRecursively(tmpDir);
        } catch (IOException e) {
            log.info("Failed to clean up temporary directory: {}", tmpDir, e);
        }
    }
}
