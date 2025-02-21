package it.gov.pagopa.payhub.activities.config;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/mail-templates.properties")
@ConfigurationProperties(prefix= "email.templates")
@Data
public class EmailTemplatesConfiguration {
    private String mailTextLoadOk;
    private String mailTextLoadKo;

    @NestedConfigurationProperty
    private IngestionFlowEmailOutcomeTemplates paymentsReportingFlow;
    @NestedConfigurationProperty
    private IngestionFlowEmailOutcomeTemplates treasuryOpiFlow;
    @NestedConfigurationProperty
    private IngestionFlowEmailOutcomeTemplates dpInstallmentsFlow;

    @Data
    public static class IngestionFlowEmailOutcomeTemplates {
        @NestedConfigurationProperty
        private EmailTemplate ok;
        @NestedConfigurationProperty
        private EmailTemplate ko;
    }
}