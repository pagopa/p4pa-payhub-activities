package it.gov.pagopa.payhub.activities.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix= "email.templates")
@Data
public class EmailTemplatesConfiguration {
    private EmailTemplate paymentsReportingFlowOk;
    private EmailTemplate paymentsReportingFlowKo;
    private String mailTextLoadOk;
    private String mailTextLoadKo;
}