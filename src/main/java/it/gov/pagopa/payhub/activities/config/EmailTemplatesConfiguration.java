package it.gov.pagopa.payhub.activities.config;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/mail-templates.properties")
@ConfigurationProperties(prefix= "email.templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplatesConfiguration {
    private String mailTextLoadOk;
    private String mailTextLoadKo;

    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates paymentsReportingFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates treasuryOpiFlow;
    @NestedConfigurationProperty
    private EmailTemplate receivedPagopaReceipt;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates dpInstallmentsFlow;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailOutcomeBasedTemplates {
        @NestedConfigurationProperty
        private EmailTemplate ok;
        @NestedConfigurationProperty
        private EmailTemplate ko;
    }
}