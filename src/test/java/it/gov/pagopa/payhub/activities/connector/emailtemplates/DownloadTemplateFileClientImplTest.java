package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DownloadTemplateFileClientImplTest {

    public static final String TEMPLATE_URL = "TEMPLATE_URL";
    public static final String FILENAME = "filename";
    public static final URI FILE_TEMPLATE_URI = URI.create(TEMPLATE_URL + "/" + FILENAME);
    @Mock
    private RestTemplate restTemplateMock;

    private DownloadTemplateFileClientImpl downloadTemplateFileClient;

    @BeforeEach
    void setUp() {
        downloadTemplateFileClient = new DownloadTemplateFileClientImpl();
        ReflectionTestUtils.setField(downloadTemplateFileClient, "restTemplate", restTemplateMock);
    }

    @Test
    void givenValidUrlAndAvailableFileWhenDownloadTemplateFileThenReturnFileContent() {
        byte[] expectedContent = "dummy-file-content".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(expectedContent);
        Mockito.when(restTemplateMock.getForEntity(URI.create("TEMPLATE_URL"+"/"+"filename"), byte[].class)).thenReturn(responseEntity);

        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadTemplateFile(TEMPLATE_URL, FILENAME);

        Assertions.assertTrue(actualContent.isPresent());
        Assertions.assertEquals(expectedContent, actualContent.get());
    }

    @Test
    void givenEmptyResponseBodyWhenDownloadFileFromSignedUrlThenReturnOptionalEmpty() {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(null);
        Mockito.when(restTemplateMock.getForEntity(FILE_TEMPLATE_URI, byte[].class)).thenReturn(responseEntity);

        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadTemplateFile(TEMPLATE_URL, FILENAME);

        Assertions.assertTrue(actualContent.isEmpty());
    }

    @Test
    void given5xxResponseStatusCodeWhenDownloadFileFromSignedUrlThenReturnOptionalEmpty() {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.internalServerError().body(null);
        Mockito.when(restTemplateMock.getForEntity(FILE_TEMPLATE_URI, byte[].class)).thenReturn(responseEntity);

        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadTemplateFile(TEMPLATE_URL, FILENAME);

        Assertions.assertTrue(actualContent.isEmpty());
    }

    @Test
    void givenExceptionWhenDownloadFileFromSignedUrlThenReturnOptionalEmpty() {
        Mockito.when(restTemplateMock.getForEntity(FILE_TEMPLATE_URI, byte[].class)).thenThrow(new RestClientException("error"));

        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadTemplateFile(TEMPLATE_URL, FILENAME);

        Assertions.assertTrue(actualContent.isEmpty());
    }
}