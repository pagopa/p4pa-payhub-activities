package it.gov.pagopa.payhub.activities.connector.printnotice;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Lazy
@Slf4j
@Service
public class SignedUrlServiceImpl implements SignedUrlService {
    private final RestTemplate noRedirectRestTemplate;

    public SignedUrlServiceImpl() {
        this.noRedirectRestTemplate = createNoRedirectRestTemplate();
    }

    private RestTemplate createNoRedirectRestTemplate() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableRedirectHandling()
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    @Override
    public byte[] downloadFileFromSignedUrl(String signedUrl) {
        URI uri = URI.create(signedUrl);

        ResponseEntity<byte[]> response = noRedirectRestTemplate.getForEntity(uri, byte[].class);
        if (response.getBody() == null) {
            throw new IllegalStateException(String.format("[INVALID_FILE_EMPTY] Downloaded file from signed url: %s", signedUrl));
        }

        return response.getBody();
    }
}
