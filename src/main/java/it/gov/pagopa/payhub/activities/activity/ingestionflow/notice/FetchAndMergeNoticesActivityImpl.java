package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.connector.printnotice.SignedUrlService;
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
import java.util.*;
import java.util.zip.ZipOutputStream;

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

            String mergedFileName = buildNoticeFileName(ingestionFlowFile);
            Path tmpZipFilePath = tmpDir.resolve(mergedFileName);

            Path sharedTargetPath = buildArchiveTargetPath(ingestionFlowFile);

            int processedFiles = 0;
            Set<String> usedZipEntryNames = new HashSet<>();

            try (ZipOutputStream zipOutputStream = fileArchiverService.createZipOutputStream(tmpZipFilePath)) {
                for (int i = 0; i < signedUrls.size(); i++) {
                    Path extractDirPath = tmpDir.resolve("extracted_" + i);

                    List<Path> extractedNotices = downloadAndExtractNotices(signedUrls.get(i), tmpDir, i);

                    for (Path noticePath : extractedNotices) {
                        String zipEntryName = buildUniqueZipEntryName(noticePath, usedZipEntryNames);
                        fileArchiverService.addToZip(zipOutputStream, noticePath, zipEntryName);
                        processedFiles++;
                    }

                    cleanupTmpDir(extractDirPath);
                }
            }

            if (processedFiles == 0) {
                return 0;
            }

            fileArchiverService.encryptAndArchiveZip(tmpZipFilePath, sharedTargetPath);

            return processedFiles;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot process and merge notices in working directory: " + tmpDir, e);
        } finally {
            cleanupTmpDir(tmpDir);
        }
    }

    private Path buildArchiveTargetPath(IngestionFlowFile file) {
        return FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), file.getOrganizationId())
                .resolve(file.getFilePathName())
                .resolve(foldersPathsConfig.getProcessTargetSubFolders().getArchive());
    }

    private String buildUniqueZipEntryName(Path filePath, Set<String> usedNames) {
        String fileName = filePath.getFileName().toString();

        if (usedNames.add(fileName)) {
            return fileName;
        }

        String baseName = fileName;
        String extension = "";

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        int counter = 1;
        String candidate;

        do {
            candidate = baseName + "_" + counter + extension;
            counter++;
        } while (!usedNames.add(candidate));

        return candidate;
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
            String trimmedFolderId = folderId.trim();
            SignedUrlResultDTO dto = printPaymentNoticeService.getSignedUrl(organizationId, trimmedFolderId);
            if (dto == null) {
                log.info("File not ready for folderId: {}", trimmedFolderId);
                return Collections.emptyList();
            }
            urls.add(dto.getSignedUrl());
        }

        return urls;
    }

    private List<Path> downloadAndExtractNotices(String url, Path tmpDir, int index) {
        byte[] downloadedBytes = signedUrlService.downloadFileFromSignedUrl(url);

        Path downloadedZipPath = tmpDir.resolve("downloaded_" + index + ".zip");
        Path extractDirPath = tmpDir.resolve("extracted_" + index);

        try {
            Files.write(downloadedZipPath, downloadedBytes);
            return zipFileService.unzip(downloadedZipPath, extractDirPath);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing or extracting zip: " + downloadedZipPath, e);
        } finally {
            try {
                Files.deleteIfExists(downloadedZipPath);
            } catch (IOException e) {
                log.info("Failed to delete downloaded temporary zip: {}", downloadedZipPath, e);
            }
        }
    }

    public static String buildNoticeFileName(IngestionFlowFile file) {
        return file.getFileName().replace(".zip", "_notice.zip");
    }

    private void cleanupTmpDir(Path tmpDir) {
        try {
            FileSystemUtils.deleteRecursively(tmpDir);
        } catch (IOException e) {
            log.info("Failed to clean up temporary directory: {}", tmpDir, e);
        }
    }
}
