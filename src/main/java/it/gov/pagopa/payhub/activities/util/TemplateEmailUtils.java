package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TemplateEmailUtils {

    public static final String TEMPLATE_HTML_FILENAME = "index.html";
    public static final String ATTACHMENTS_FILENAME = "attachments.html";

    private TemplateEmailUtils() {}

    public static Path buildTemplateFolderPath(String templateFolderBasePath, String brokerExternalId, EmailTemplateName templateName) {
        return Path.of(StringUtils.joinWith("/", templateFolderBasePath, brokerExternalId, templateName.name()));
    }

    public static String buildTemplateRepoUrl(String templateRepoBaseUrl, String brokerExternalId, EmailTemplateName templateName) {
        return StringUtils.joinWith("/", templateRepoBaseUrl, brokerExternalId, templateName.name());
    }

    public static List<String> splitAttachmentFileNames(byte[] attachmentFileResource) {
        if(attachmentFileResource == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(new String(attachmentFileResource).split("\n"))
                .map(String::trim)
                .toList();
    }

}
