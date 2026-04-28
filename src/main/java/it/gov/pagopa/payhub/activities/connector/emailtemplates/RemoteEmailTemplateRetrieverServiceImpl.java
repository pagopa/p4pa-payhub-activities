package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import it.gov.pagopa.payhub.activities.connector.emailtemplates.client.DownloadEmailTemplateClient;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.dto.email.SerializableFileResourceDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Lazy
@Slf4j
public class RemoteEmailTemplateRetrieverServiceImpl implements RemoteEmailTemplateRetrieverService {

    private static final String TEMPLATE_HTML_FILENAME = "index.html";
    private static final String ATTACHMENTS_FILENAME = "attachments.html";

    private final Map<String, Boolean> templateNotFoundOnRepoMap = new ConcurrentHashMap<>();

    private final DownloadEmailTemplateClient downloadEmailTemplateClient;

    public RemoteEmailTemplateRetrieverServiceImpl(DownloadEmailTemplateClient downloadEmailTemplateClient) {
        this.downloadEmailTemplateClient = downloadEmailTemplateClient;
    }

    @Override
    public EmailTemplate retrieve(String brokerExternalId, EmailTemplateName templateName, String emailSubject) {
        if(isTemplateAlreadyNotFound(brokerExternalId, templateName)) {
            return null;
        }
        Optional<byte[]> emailTemplateBytes = downloadEmailTemplateClient.downloadEmailTemplate(brokerExternalId, templateName, TEMPLATE_HTML_FILENAME);
        if(emailTemplateBytes.isEmpty()) {
            templateNotFoundOnRepoMap.put(buildMapKey(brokerExternalId, templateName), true);
            return null;
        }
        List<SerializableFileResourceDTO> attachmentFiles = fetchAllAttachments(brokerExternalId, templateName);
        return new EmailTemplate(
                emailSubject,
                new String(emailTemplateBytes.get()),
                attachmentFiles
        );
    }

    private boolean isTemplateAlreadyNotFound(String brokerExternalId, EmailTemplateName templateName) {
        return Optional.ofNullable(templateNotFoundOnRepoMap.get(buildMapKey(brokerExternalId, templateName)))
                .orElse(false);
    }

    private String buildMapKey(String brokerExternalId, EmailTemplateName templateName) {
        return StringUtils.joinWith("-", brokerExternalId, templateName.name());
    }

    private List<SerializableFileResourceDTO> fetchAllAttachments(String brokerExternalId, EmailTemplateName emailTemplateName) {
        Optional<byte[]> attachmentsFileBytes = downloadEmailTemplateClient.downloadEmailTemplate(brokerExternalId, emailTemplateName, ATTACHMENTS_FILENAME);
        if(attachmentsFileBytes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> attachmentFileNames = splitAttachmentFilenames(attachmentsFileBytes.get());
        return attachmentFileNames.stream()
                .map(filename -> {
                    Optional<byte[]> bytes = downloadEmailTemplateClient.downloadEmailTemplate(brokerExternalId, emailTemplateName, "/attachments/" + filename);
                    return bytes.map(value -> new SerializableFileResourceDTO(value, filename))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> splitAttachmentFilenames(byte[] attachmentFileResource) {
        String attachmentsFileString = new String(attachmentFileResource, StandardCharsets.UTF_8);
        if(attachmentsFileString.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(attachmentsFileString.split(System.lineSeparator()))
                .map(String::trim)
                .filter(filename -> !filename.isBlank())
                .toList();
    }

}
