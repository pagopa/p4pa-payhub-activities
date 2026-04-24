package it.gov.pagopa.payhub.activities.service.email.retriever;

import it.gov.pagopa.payhub.activities.connector.emailtemplates.DownloadTemplateFileClient;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.util.TemplateEmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Lazy
@Slf4j
public class EmailTemplateRetrieverServiceImpl implements EmailTemplateRetrieverService {

    private final String templateRepoBaseUrl;

    private final Map<String, Boolean> templateNotFoundOnRepoMap = new HashMap<>();

    private final DownloadTemplateFileClient downloadTemplateFileClient;

    public EmailTemplateRetrieverServiceImpl(
            @Value("${mail.template.repo-base-url}") String templateRepoBaseUrl,
            DownloadTemplateFileClient downloadTemplateFileClient) {
        this.templateRepoBaseUrl = templateRepoBaseUrl;
        this.downloadTemplateFileClient = downloadTemplateFileClient;
    }

    @Override
    public boolean isTemplateAlreadyNotFound(String brokerExternalId, EmailTemplateName emailTemplateName) {
        String templateRepoUrl = TemplateEmailUtils.buildTemplateRepoUrl(templateRepoBaseUrl, brokerExternalId, emailTemplateName);
        return Optional.ofNullable(templateNotFoundOnRepoMap.get(templateRepoUrl))
                .orElse(false);
    }

    @Override
    public EmailTemplate retrieve(String brokerExternalId, EmailTemplateName emailTemplateName, String emailSubject) {
        String templateRepoUrl = TemplateEmailUtils.buildTemplateRepoUrl(templateRepoBaseUrl, brokerExternalId, emailTemplateName);
        Optional<byte[]> emailTemplateBytes = downloadTemplateFileClient.downloadTemplateFile(templateRepoUrl + "/" + TemplateEmailUtils.TEMPLATE_HTML_FILENAME);
        if(emailTemplateBytes.isEmpty()) {
            templateNotFoundOnRepoMap.put(templateRepoUrl, true);
            return null;
        }
        List<FileResourceDTO> attachmentFiles = fetchAndCacheAllAttachments(templateRepoUrl);
        return new EmailTemplate(
                emailSubject,
                new String(emailTemplateBytes.get()),
                attachmentFiles
        );
    }


    private List<FileResourceDTO> fetchAndCacheAllAttachments(String templateRepoUrl) {
        Optional<byte[]> attachmentsFileBytes = downloadTemplateFileClient.downloadTemplateFile(templateRepoUrl + "/" + TemplateEmailUtils.ATTACHMENTS_FILENAME);
        if(attachmentsFileBytes.isEmpty()) {
            return null;
        }
        List<String> attachmentFileNames = TemplateEmailUtils.splitAttachmentFileNames(attachmentsFileBytes.get());
        return attachmentFileNames.stream()
                .map(filename -> {
                    Optional<byte[]> bytes = downloadTemplateFileClient.downloadTemplateFile(templateRepoUrl + "/attachments/" + filename);
                    return bytes.map(value -> new FileResourceDTO(new ByteArrayResource(value), filename))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
