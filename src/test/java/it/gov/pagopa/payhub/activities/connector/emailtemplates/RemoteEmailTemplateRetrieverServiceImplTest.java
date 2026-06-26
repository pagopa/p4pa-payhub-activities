package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import it.gov.pagopa.payhub.activities.connector.emailtemplates.client.DownloadEmailTemplateClient;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RemoteEmailTemplateRetrieverServiceImplTest {

    private static final String TEMPLATE_HTML_FILENAME = "index.html";
    private static final String ATTACHMENTS_FILENAME = "attachments.txt";


    private static final String BROKER_EXTERNAL_ID = "BROKER_EXTERNAL_ID";
    private static final EmailTemplateName TEMPLATE_NAME = EmailTemplateName.INGESTION_RECEIPT_OK;
    private static final String EMAIL_SUBJECT = "EMAIL_SUBJECT";

    private static final EmailTemplateName NOT_FOUND_TEMPLATE_NAME = EmailTemplateName.INGESTION_RECEIPT_KO;

    @Mock
    private DownloadEmailTemplateClient downloadEmailTemplateClientMock;

    private RemoteEmailTemplateRetrieverServiceImpl retrieverService;

    @BeforeEach
    void setup() {
        retrieverService = new RemoteEmailTemplateRetrieverServiceImpl(
                downloadEmailTemplateClientMock
        );
        loadInMapNotFoundTemplate();
    }

    private void loadInMapNotFoundTemplate() {
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, NOT_FOUND_TEMPLATE_NAME, TEMPLATE_HTML_FILENAME))
                .thenReturn(Optional.empty());
        retrieverService.retrieve(BROKER_EXTERNAL_ID, NOT_FOUND_TEMPLATE_NAME, EMAIL_SUBJECT);
        Mockito.reset(downloadEmailTemplateClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                downloadEmailTemplateClientMock
        );
    }

    @Test
    void givenIsTemplateAlreadyNotFoundWhenRetrieveThenReturnNull() {
        //GIVEN
        //WHEN
        EmailTemplate actualEmailTemplate = retrieverService.retrieve(BROKER_EXTERNAL_ID, NOT_FOUND_TEMPLATE_NAME, EMAIL_SUBJECT);
        //THEN
        Assertions.assertNull(actualEmailTemplate);
        Mockito.verify(downloadEmailTemplateClientMock, Mockito.times(0))
                .downloadEmailTemplate(BROKER_EXTERNAL_ID, NOT_FOUND_TEMPLATE_NAME, TEMPLATE_HTML_FILENAME);
    }

    @Test
    void givenIsTemplateNotInMapAndNotFoundOnRepoWhenRetrieveThenReturnNull() {
        //GIVEN
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, TEMPLATE_HTML_FILENAME))
                .thenReturn(Optional.empty());
        //WHEN
        EmailTemplate actualEmailTemplate = retrieverService.retrieve(BROKER_EXTERNAL_ID, TEMPLATE_NAME, EMAIL_SUBJECT);
        //THEN
        Assertions.assertNull(actualEmailTemplate);
        Mockito.verify(downloadEmailTemplateClientMock)
                .downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, TEMPLATE_HTML_FILENAME);
    }

    @Test
    void givenTemplateFoundOnRepoWithoutAttachmentsFileWhenRetrieveThenReturnEmailWithEmptyInlines() {
        //GIVEN
        String expectedFileContent = "file content";
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, TEMPLATE_HTML_FILENAME))
                .thenReturn(Optional.of(expectedFileContent.getBytes()));
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, ATTACHMENTS_FILENAME))
                .thenReturn(Optional.empty());
        //WHEN
        EmailTemplate actualEmailTemplate = retrieverService.retrieve(BROKER_EXTERNAL_ID, TEMPLATE_NAME, EMAIL_SUBJECT);
        //THEN
        Assertions.assertNotNull(actualEmailTemplate);
        Assertions.assertEquals(EMAIL_SUBJECT, actualEmailTemplate.getSubject());
        Assertions.assertEquals(expectedFileContent, actualEmailTemplate.getBody());
        Assertions.assertTrue(actualEmailTemplate.getInlines().isEmpty());
        Mockito.verify(downloadEmailTemplateClientMock)
                .downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, TEMPLATE_HTML_FILENAME);
        Mockito.verify(downloadEmailTemplateClientMock)
                .downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, ATTACHMENTS_FILENAME);
    }

    @Test
    void givenTemplateFoundOnRepoWithEmptyAttachmentsFileWhenRetrieveThenReturnEmailWithEmptyInlines() {
        //GIVEN
        String expectedFileContent = "file content";
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, TEMPLATE_HTML_FILENAME))
                .thenReturn(Optional.of(expectedFileContent.getBytes()));
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, ATTACHMENTS_FILENAME))
                .thenReturn(Optional.of(new byte[0]));
        //WHEN
        EmailTemplate actualEmailTemplate = retrieverService.retrieve(BROKER_EXTERNAL_ID, TEMPLATE_NAME, EMAIL_SUBJECT);
        //THEN
        Assertions.assertNotNull(actualEmailTemplate);
        Assertions.assertEquals(EMAIL_SUBJECT, actualEmailTemplate.getSubject());
        Assertions.assertEquals(expectedFileContent, actualEmailTemplate.getBody());
        Assertions.assertTrue(actualEmailTemplate.getInlines().isEmpty());
        Mockito.verify(downloadEmailTemplateClientMock)
                .downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, TEMPLATE_HTML_FILENAME);
        Mockito.verify(downloadEmailTemplateClientMock)
                .downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, ATTACHMENTS_FILENAME);
    }

    @Test
    void givenTemplateFoundOnRepoWhenRetrieveThenReturnEmailWithInlines() {
        //GIVEN
        String expectedFileContent = "file content";
        String attachmentsFileContent = " file1.txt \n\r\n\t file2.txt \n../file3.txt";
        String expectedAttachmentFile1Content = "attachment file1 content";
        String expectedAttachmentFile2Content = "attachment file2 content";
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, TEMPLATE_HTML_FILENAME))
                .thenReturn(Optional.of(expectedFileContent.getBytes(StandardCharsets.UTF_8)));
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, ATTACHMENTS_FILENAME))
                .thenReturn(Optional.of(attachmentsFileContent.getBytes(StandardCharsets.UTF_8)));
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, "/attachments/file1.txt"))
                .thenReturn(Optional.of(expectedAttachmentFile1Content.getBytes(StandardCharsets.UTF_8)));
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, "/attachments/file2.txt"))
                .thenReturn(Optional.of(expectedAttachmentFile2Content.getBytes(StandardCharsets.UTF_8)));
        Mockito.when(downloadEmailTemplateClientMock.downloadEmailTemplate(BROKER_EXTERNAL_ID, TEMPLATE_NAME, "/attachments/_file3.txt"))
                .thenReturn(Optional.empty());
        //WHEN
        EmailTemplate actualEmailTemplate = retrieverService.retrieve(BROKER_EXTERNAL_ID, TEMPLATE_NAME, EMAIL_SUBJECT);
        //THEN
        Assertions.assertNotNull(actualEmailTemplate);
        Assertions.assertEquals(EMAIL_SUBJECT, actualEmailTemplate.getSubject());
        Assertions.assertEquals(expectedFileContent, actualEmailTemplate.getBody());
        Assertions.assertEquals(2, actualEmailTemplate.getInlines().size());
        List<String> attachmentsFileContents = getAttachmentFileContents(actualEmailTemplate);
        Assertions.assertTrue(attachmentsFileContents.contains(expectedAttachmentFile1Content));
        Assertions.assertTrue(attachmentsFileContents.contains(expectedAttachmentFile2Content));
    }

    private static List<String> getAttachmentFileContents(EmailTemplate actualEmailTemplate) {
        return actualEmailTemplate.getInlines()
                .stream()
                .map(res -> new String(res.getFileContent()))
                .toList();
    }

}