package it.gov.pagopa.payhub.activities.config;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/mail-templates.properties")
@ConfigurationProperties(prefix= "email.templates")
@Data
public class EmailTemplatesConfiguration {
    private EmailTemplate paymentsReportingFlowOk;
    private EmailTemplate paymentsReportingFlowKo;
    private String mailTextLoadOk;
    private String mailTextLoadKo;
}