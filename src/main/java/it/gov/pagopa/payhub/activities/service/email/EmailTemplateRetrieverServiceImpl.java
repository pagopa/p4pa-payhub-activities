package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.connector.emailtemplates.DownloadTemplateFileClient;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Lazy
@Service
@Slf4j
public class EmailTemplateRetrieverServiceImpl implements EmailTemplateRetrieverService {

    private static final String INDEX_HTML = "index.html";

    @Value("${mail.template.repo-base-url}")
    private String templateRepoBaseUrl;
    @Value("${mail.template.folder-base-path}")
    private String templateFolderBasePath;
    
    private final Map<String, Boolean> templateFoundOnRepo = new HashMap<>();

    private final DownloadTemplateFileClient downloadTemplateFileClient;

    public EmailTemplateRetrieverServiceImpl(DownloadTemplateFileClient downloadTemplateFileClient) {
        this.downloadTemplateFileClient = downloadTemplateFileClient;
    }

    @Override
    public EmailTemplate retrieveTemplate(String brokerExternalId, EmailTemplateName templateName, String emailSubject) {
        if(brokerExternalId == null) {
            return null;
        }
        String templateRepoUrl = buildTemplateRepoUrl(brokerExternalId, templateName);
        Boolean isTemplateFoundOnRepo = templateFoundOnRepo.get(templateRepoUrl);
        if(isTemplateFoundOnRepo != null && !isTemplateFoundOnRepo) {
            return null;
        }
        Path templateFolderPath = buildTemplateFolderPath(brokerExternalId, templateName);
        if(Files.exists(Path.of(templateFolderPath + "/" + INDEX_HTML))) {
            return fetchCachedEmailTemplate(emailSubject, templateFolderPath);
        } else {
            return fetchAndCacheEmailTemplate(emailSubject, templateRepoUrl, templateFolderPath);
        }
    }

    private EmailTemplate fetchAndCacheEmailTemplate(String emailSubject, String templateRepoUrl, Path templateFolderPath) {
        byte[] emailTemplateBytes = fetchAndCacheEmailTemplateFile(templateRepoUrl, templateFolderPath, "index.html");
        if(emailTemplateBytes == null || emailTemplateBytes.length == 0) {
            templateFoundOnRepo.put(templateRepoUrl, false);
            return null;
        }
        templateFoundOnRepo.put(templateRepoUrl, true);
        List<FileResourceDTO> attachmentFiles = fetchAndCacheAllAttachments(templateRepoUrl, templateFolderPath);
        return new EmailTemplate(
                emailSubject,
                new String(emailTemplateBytes),
                attachmentFiles
        );
    }

    private EmailTemplate fetchCachedEmailTemplate(String emailSubject, Path templateFolderPath) {
        byte[] emailTemplateBytes = fetchCachedEmailTemplateFile(templateFolderPath, INDEX_HTML);
        if(emailTemplateBytes == null || emailTemplateBytes.length == 0) return null;
        List<FileResourceDTO> attachmentFiles = fetchCachedAttachments(templateFolderPath);
        return new EmailTemplate(
                emailSubject,
                new String(emailTemplateBytes),
                attachmentFiles
        );
    }

    private List<FileResourceDTO> fetchAndCacheAllAttachments(String templateRepoUrl, Path templateFolderPath) {
        List<FileResourceDTO> attachmentFiles = new ArrayList<>();
        byte[] attachmentFilesBytes = fetchAndCacheEmailTemplateFile(templateRepoUrl, templateFolderPath, "attachment.txt");
        if(attachmentFilesBytes !=null) {
            List<String> attachmentFileNames = splitAttachmentFileNames(attachmentFilesBytes);
            attachmentFiles = attachmentFileNames.stream()
                    .map(filename -> {
                        byte[] bytes = fetchAndCacheEmailTemplateFile(templateRepoUrl, templateFolderPath, filename);
                        if (bytes == null || bytes.length == 0) return null;
                        return new FileResourceDTO(new ByteArrayResource(bytes), filename);
                    })
                    .filter(Objects::nonNull)
                    .toList();
        }
        return attachmentFiles;
    }

    private List<FileResourceDTO> fetchCachedAttachments(Path templateFolderPath) {
        List<FileResourceDTO> attachmentFiles = new ArrayList<>();
        byte[] attachmentFilesBytes = fetchCachedEmailTemplateFile(templateFolderPath, "attachment.txt");
        for (String attachmentFilename : splitAttachmentFileNames(attachmentFilesBytes)) {
            byte[] bytes = fetchCachedEmailTemplateFile(templateFolderPath, attachmentFilename);
            if(bytes == null || bytes.length == 0) continue;
            attachmentFiles.add(
                    new FileResourceDTO(
                            new ByteArrayResource(bytes),
                            attachmentFilename
                    )
            );
        }
        return attachmentFiles;
    }

    private byte[] fetchAndCacheEmailTemplateFile(String templateRepoUrl, Path templateFolderPath, String fileName) {
        Optional<byte[]> templateFileResource = downloadTemplateFileClient.downloadTemplateFile(templateRepoUrl, fileName);
        if(templateFileResource.isEmpty()) {
            return null;
        }
        try (FileOutputStream fos = new FileOutputStream(templateFolderPath.getFileName() + "/" + fileName)) {
            if(!Files.isDirectory(templateFolderPath)) {
                Files.createDirectories(templateFolderPath);
            }
            fos.write(templateFileResource.get());
        } catch (IOException e) {
            log.warn("Error in saving file \"%s\" into folder \"%s\": %s".formatted(fileName, templateFolderPath.getFileName(), e.getMessage()));
        }
        return templateFileResource.get();
    }

    private byte[] fetchCachedEmailTemplateFile(Path templateFolderPath, String fileName) {
        try (FileInputStream fis = new FileInputStream(templateFolderPath.getFileName() + "/" + fileName)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            log.warn("Error in reading file \"%s\" from folder \"%s\": %s".formatted(fileName, templateFolderPath.getFileName(), e.getMessage()));
            return null;
        }
    }

    private Path buildTemplateFolderPath(String brokerExternalId, EmailTemplateName templateName) {
        return Path.of(StringUtils.joinWith("/", templateFolderBasePath, brokerExternalId, templateName.name()));
    }

    private String buildTemplateRepoUrl(String brokerExternalId, EmailTemplateName templateName) {
        return StringUtils.joinWith("/", templateRepoBaseUrl, brokerExternalId, templateName.name());
    }

    private List<String> splitAttachmentFileNames(byte[] attachmentFileResource) {
        if(attachmentFileResource == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(new String(attachmentFileResource).split("\n"))
                .map(String::trim)
                .toList();
    }

}
