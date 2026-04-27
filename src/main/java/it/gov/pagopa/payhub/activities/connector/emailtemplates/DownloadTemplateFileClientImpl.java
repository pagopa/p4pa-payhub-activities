package it.gov.pagopa.payhub.activities.connector.emailtemplates;

import it.gov.pagopa.payhub.activities.exception.RetryableActivityException;
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
    public Optional<byte[]> downloadTemplateFile(String templateFileUrl) {
        ResponseEntity<byte[]> response = getFromPublicUri(URI.create(templateFileUrl));
        if(response.getStatusCode().value() == 404) {
            return Optional.empty(); // if template not found return empty optional for communicating its absence
        }
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RetryableActivityException("Error in downloading template file \"%s\"".formatted(templateFileUrl));
        }
        return Optional.of(response.getBody());
    }

    private ResponseEntity<byte[]> getFromPublicUri(URI publicUri) {
        try {
            return restTemplate.getForEntity(publicUri, byte[].class);
        } catch (RestClientException e) {
            log.error("Error in GET call to URI \"{}\": {}", publicUri, e.getMessage());
            throw new RetryableActivityException("Error in GET call to URI \"%s\"".formatted(publicUri), e);
        }
    }

}
