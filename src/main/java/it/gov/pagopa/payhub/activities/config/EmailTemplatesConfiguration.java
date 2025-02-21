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
    @NestedConfigurationProperty
    private EmailTemplate paymentsReportingFlowOk;
    @NestedConfigurationProperty
    private EmailTemplate paymentsReportingFlowKo;
    @NestedConfigurationProperty
    private EmailTemplate treasuryOpiFlowOk;
    @NestedConfigurationProperty
    private EmailTemplate treasuryOpiFlowKo;
    @NestedConfigurationProperty
    private EmailTemplate receivedPagopaReceipt;
    private String mailTextLoadOk;
    private String mailTextLoadKo;
}