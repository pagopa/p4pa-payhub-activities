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
    private String exportMailTextOk;
    private String exportMailTextKo;

    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates paymentsReportingFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates paymentNotificationFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates treasuryOpiFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates treasuryPosteFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates treasuryXlsFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates treasuryCsvFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates treasuryCsvCompleteFlow;
    @NestedConfigurationProperty
    private EmailTemplate receivedPagopaReceipt;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates dpInstallmentsFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates organizationsFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates treasuryCsvCompleteFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates organizationsSilServiceFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates debtPositionsTypeFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates debtPositionsTypeOrgFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates debtPositionsTypeOrgOperatorsFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates assessmentsFlow;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates receivedReceipt;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates exportPaidFile;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates exportReceiptsArchivingFile;
    @NestedConfigurationProperty
    private EmailOutcomeBasedTemplates exportClassificationsFile;

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