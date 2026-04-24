package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import java.util.Optional;

public interface DownloadTemplateFileClient {
    Optional<byte[]> downloadTemplateFile(String templateRepoUrl, String filename);
}
