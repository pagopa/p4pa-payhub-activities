package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import it.gov.pagopa.payhub.activities.exception.RetryableActivityException;
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

        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadTemplateFile(FILE_TEMPLATE_URI.toString());

        Assertions.assertTrue(actualContent.isPresent());
        Assertions.assertEquals(expectedContent, actualContent.get());
    }

    @Test
    void given404StatusCodeWhenDownloadTemplateFileThenReturnOptionalEmpty() {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.status(404).build();
        Mockito.when(restTemplateMock.getForEntity(FILE_TEMPLATE_URI, byte[].class)).thenReturn(responseEntity);

        Optional<byte[]> actualContent = downloadTemplateFileClient.downloadTemplateFile(FILE_TEMPLATE_URI.toString());

        Assertions.assertTrue(actualContent.isEmpty());
    }

    @Test
    void given5xxResponseStatusCodeWhenDownloadTemplateFileThenThrowsException() {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.internalServerError().body(null);
        Mockito.when(restTemplateMock.getForEntity(FILE_TEMPLATE_URI, byte[].class)).thenReturn(responseEntity);

        String fileTemplateUriString = FILE_TEMPLATE_URI.toString();
        RetryableActivityException retryableActivityException =
                Assertions.assertThrows(RetryableActivityException.class, () -> downloadTemplateFileClient.downloadTemplateFile(fileTemplateUriString));

        Assertions.assertEquals("Error in downloading template file \"%s\"".formatted(FILE_TEMPLATE_URI), retryableActivityException.getMessage());
    }

    @Test
    void givenExceptionWhenWhenDownloadTemplateFileThenThrowsException() {
        RestClientException expectedCause = new RestClientException("error");
        Mockito.when(restTemplateMock.getForEntity(FILE_TEMPLATE_URI, byte[].class)).thenThrow(expectedCause);

        String fileTemplateUriString = FILE_TEMPLATE_URI.toString();
        RetryableActivityException retryableActivityException =
                Assertions.assertThrows(RetryableActivityException.class, () -> downloadTemplateFileClient.downloadTemplateFile(fileTemplateUriString));

        Assertions.assertEquals("Error in GET call to URI \"%s\"".formatted(FILE_TEMPLATE_URI), retryableActivityException.getMessage());
        Assertions.assertNotNull(retryableActivityException.getCause());
        Assertions.assertEquals(expectedCause, retryableActivityException.getCause());
    }
}