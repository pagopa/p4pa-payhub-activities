package it.gov.pagopa.payhub.activities.connector.signedurl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@ExtendWith(MockitoExtension.class)
class SignedUrlImplTest {
    @Mock
    private RestTemplate restTemplateMock;

    private SignedUrlServiceImpl signedUrlService;

    private static final String SIGNED_URL = "http://url";
    private static final URI DUMMY_URI = URI.create(SIGNED_URL);

    @BeforeEach
    void setUp() {
        signedUrlService = new SignedUrlServiceImpl();
        ReflectionTestUtils.setField(signedUrlService, "noRedirectRestTemplate", restTemplateMock);
    }

    @Test
    void givenValidUrlAndAvailableFileWhenDownloadFileFromSignedUrlThenReturnFileContent() {
        byte[] expectedContent = "dummy-file-content".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(expectedContent);
        Mockito.when(restTemplateMock.getForEntity(DUMMY_URI, byte[].class)).thenReturn(responseEntity);

        byte[] actualContent = signedUrlService.downloadFileFromSignedUrl(SIGNED_URL);

        Assertions.assertEquals(expectedContent, actualContent);
    }

    @Test
    void givenEmptyResponseBodyWhenDownloadFileFromSignedUrlThenThrowsIllegalStateException() {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(null);
        Mockito.when(restTemplateMock.getForEntity(DUMMY_URI, byte[].class)).thenReturn(responseEntity);

       Assertions.assertThrows(IllegalStateException.class, () ->
                signedUrlService.downloadFileFromSignedUrl(SIGNED_URL)
       );
    }
}
