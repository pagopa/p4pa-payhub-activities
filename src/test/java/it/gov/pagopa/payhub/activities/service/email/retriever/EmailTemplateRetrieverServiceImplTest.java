package it.gov.pagopa.payhub.activities.service.email.retriever;

import it.gov.pagopa.payhub.activities.connector.emailtemplates.DownloadTemplateFileClient;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.util.TemplateEmailUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmailTemplateRetrieverServiceImplTest {

    public static final String TEMPLATE_REPO_BASE_URL = "http://pagopa.local.it";
    @Mock
    private DownloadTemplateFileClient downloadTemplateFileClientMock;

    private EmailTemplateRetrieverServiceImpl retrieverService;

    @BeforeEach
    void setup() {
        retrieverService = new EmailTemplateRetrieverServiceImpl(
                TEMPLATE_REPO_BASE_URL,
                downloadTemplateFileClientMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                downloadTemplateFileClientMock
        );
    }

    @Test
    void isTemplateAlreadyNotFound() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        //WHEN
        retrieverService.isTemplateAlreadyNotFound(brokerExternalId, templateName);
        //THEN
    }

    @Test
    void retrieve() {
        //GIVEN
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        EmailTemplateName templateName = EmailTemplateName.INGESTION_RECEIPT_OK;
        String emailSubject = "EMAIL_SUBJECT";
        String templateFolderUrl = TemplateEmailUtils.buildTemplateRepoUrl(TEMPLATE_REPO_BASE_URL, brokerExternalId, templateName);
        Mockito.when(downloadTemplateFileClientMock.downloadTemplateFile(templateFolderUrl + "/" + TemplateEmailUtils.TEMPLATE_HTML_FILENAME))
                .thenReturn(Optional.of(new byte[0]));
        Mockito.when(downloadTemplateFileClientMock.downloadTemplateFile(templateFolderUrl + "/" + TemplateEmailUtils.ATTACHMENTS_FILENAME))
                .thenReturn(Optional.of("logo.png".getBytes()));
        Mockito.when(downloadTemplateFileClientMock.downloadTemplateFile(templateFolderUrl + "/attachments/logo.png"))
                .thenReturn(Optional.of(new byte[0]));
        //WHEN
        EmailTemplate actualEmailTemplate = retrieverService.retrieve(brokerExternalId, templateName, emailSubject);
        //THEN
        Assertions.assertNotNull(actualEmailTemplate);
    }

    //TODO-4599 added remaining test cases
}