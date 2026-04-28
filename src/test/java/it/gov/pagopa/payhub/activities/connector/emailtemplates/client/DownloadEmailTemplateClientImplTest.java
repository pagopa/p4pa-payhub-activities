package it.gov.pagopa.payhub.activities.connector.emailtemplates.client;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.RetryableActivityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DownloadEmailTemplateClientImplTest {

    public static final String TEMPLATE_REPO_BASE_URL = "http://pagopa.local.it";
    private static final String BROKER_EXTERNAL_ID = "BROKER_EXTERNAL_ID";
    private static final EmailTemplateName EMAIL_TEMPLATE_NAME = EmailTemplateName.INGESTION_RECEIPT_OK;
    private static final String RELATIVE_FILE_PATH = "RELATIVE/FILE_PATH";
    public static final String URL = TEMPLATE_REPO_BASE_URL + "/" + BROKER_EXTERNAL_ID + "/" + EMAIL_TEMPLATE_NAME + "/" + RELATIVE_FILE_PATH;

    @Mock
    private RestTemplate restTemplateMock;

    private DownloadEmailTemplateClientImpl downloadTemplateFileClient;

    @BeforeEach
    void setUp() {
        downloadTemplateFileClient = new DownloadEmailTemplateClientImpl(TEMPLATE_REPO_BASE_URL);
        ReflectionTestUtils.setField(downloadTemplateFileClient, "restTemplate", restTemplateMock);
    }

    @Test
    void givenValidUrlAndAvailableFileWhenDownloadEmailTemplateFileThenReturnContent() {
        //GIVEN
        byte[] expectedContent = "dummy-file-content".getBytes(StandardCharsets.UTF_8);
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(expectedContent);
        Mockito.when(restTemplateMock.getForEntity(URL, byte[].class))
                .thenReturn(responseEntity);
        //WHEN
        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadEmailTemplate(BROKER_EXTERNAL_ID, EMAIL_TEMPLATE_NAME, RELATIVE_FILE_PATH);
        //THEN
        Assertions.assertTrue(actualContent.isPresent());
        Assertions.assertEquals(expectedContent, actualContent.get());
    }

    @Test
    void givenNotFoundWhenDownloadEmailTemplateThenReturnOptionalEmpty() {
        //GIVEN
        Mockito.when(restTemplateMock.getForEntity(URL, byte[].class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        //WHEN
        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadEmailTemplate(BROKER_EXTERNAL_ID, EMAIL_TEMPLATE_NAME, RELATIVE_FILE_PATH);
        //THEN
        Assertions.assertTrue(actualContent.isEmpty());
    }

    @Test
    void givenExceptionWhenWhenDownloadEmailTemplateThenThrowsException() {
        //GIVEN
        RestClientException expectedCause = new RestClientException("error");
        Mockito.when(restTemplateMock.getForEntity(URL, byte[].class)).thenThrow(expectedCause);
        //WHEN
        RetryableActivityException retryableActivityException =
                Assertions.assertThrows(RetryableActivityException.class, () -> downloadTemplateFileClient.downloadEmailTemplate(BROKER_EXTERNAL_ID, EMAIL_TEMPLATE_NAME, RELATIVE_FILE_PATH));
        //THEN
        Assertions.assertEquals("Error in GET call to URI \"%s\"".formatted(URL), retryableActivityException.getMessage());
        Assertions.assertNotNull(retryableActivityException.getCause());
        Assertions.assertEquals(expectedCause, retryableActivityException.getCause());
    }
}