package it.gov.pagopa.payhub.activities.connector.emailtemplates.client;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

import java.util.Optional;

public interface DownloadEmailTemplateClient {
    Optional<byte[]> downloadEmailTemplate(String brokerExternalId, EmailTemplateName templateName, String relativeFilePath);
}
