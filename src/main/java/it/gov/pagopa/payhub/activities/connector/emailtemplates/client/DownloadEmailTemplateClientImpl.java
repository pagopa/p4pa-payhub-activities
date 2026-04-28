package it.gov.pagopa.payhub.activities.connector.emailtemplates.client;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.RetryableActivityException;
import it.gov.pagopa.payhub.activities.performancelogger.RestInvokePerformanceLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Lazy
@Service
public class DownloadEmailTemplateClientImpl implements DownloadEmailTemplateClient {

    private final String templateRepoBaseUrl;
    private final RestTemplate restTemplate;

    public DownloadEmailTemplateClientImpl(@Value("${mail.template.repo-base-url}") String templateRepoBaseUrl) {
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableRedirectHandling()
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(new RestInvokePerformanceLogger()));
        this.templateRepoBaseUrl = templateRepoBaseUrl;
    }

    @Override
    public Optional<byte[]> downloadEmailTemplate(String brokerExternalId, EmailTemplateName templateName, String relativeFilePath) {
        String templateFileUrl = buildTemplateFileRepoUrl(brokerExternalId, templateName, relativeFilePath);
        try {
            return Optional.ofNullable(restTemplate.getForEntity(templateFileUrl, byte[].class).getBody());
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Template {} not found in repo {}: {}", templateName, templateFileUrl, e.getMessage());
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Error in GET call to URI \"{}\": {}", templateFileUrl, e.getMessage());
            throw new RetryableActivityException("Error in GET call to URI \"%s\"".formatted(templateFileUrl), e);
        }
    }

    private String buildTemplateFileRepoUrl(String brokerExternalId, EmailTemplateName templateName, String filename) {
        return StringUtils.joinWith("/", templateRepoBaseUrl, brokerExternalId, templateName.name(), filename);
    }
}
