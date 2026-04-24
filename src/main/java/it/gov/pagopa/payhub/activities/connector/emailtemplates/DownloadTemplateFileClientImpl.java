package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Lazy
@Service
public class DownloadTemplateFileClientImpl implements DownloadTemplateFileClient {

    private final RestTemplate restTemplate;

    public DownloadTemplateFileClientImpl() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableRedirectHandling()
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public Optional<byte[]> downloadTemplateFile(String templateRepoUrl, String filename) {
        URI uri = URI.create(templateRepoUrl + "/" + filename);
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(uri, byte[].class);
            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Optional.empty();
            }
            return Optional.of(response.getBody());
        } catch (RestClientException e) {
            log.warn("Error in downloading file \"%s\": %s".formatted(templateRepoUrl + "/" + filename, e.getMessage()));
            return Optional.empty();
        }
    }

}
