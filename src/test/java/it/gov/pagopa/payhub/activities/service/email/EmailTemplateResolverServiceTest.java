package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EmailTemplateResolverServiceTest {

    private final EmailTemplatesConfiguration configMock = new EmailTemplatesConfiguration(
            "MAILTEXTLOADOK",
            "MAILTEXTLOADKO",

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_PAYMENTS_REPORTING_OK_SUBJECT", "INGESTION_PAYMENTS_REPORTING_OK_BODY"),
                    new EmailTemplate("INGESTION_PAYMENTS_REPORTING_KO_SUBJECT", "INGESTION_PAYMENTS_REPORTING_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_TREASURY_OPI_OK_SUBJECT", "INGESTION_TREASURY_OPI_OK_BODY"),
                    new EmailTemplate("INGESTION_TREASURY_OPI_KO_SUBJECT", "INGESTION_TREASURY_OPI_KO_BODY")
            ),

            new EmailTemplate("INGESTION_PAGOPA_RT_SUBJECT", "INGESTION_PAGOPA_RT_BODY"),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_DP_INSTALLMENTS_OK_SUBJECT", "INGESTION_DP_INSTALLMENTS_OK_BODY"),
                    new EmailTemplate("INGESTION_DP_INSTALLMENTS_KO_SUBJECT", "INGESTION_DP_INSTALLMENTS_KO_BODY")
            )
    );

    private final EmailTemplateResolverService service = new EmailTemplateResolverService(configMock);

    @ParameterizedTest
    @EnumSource(EmailTemplateName.class)
    void givenEnumWhenResolveThenReturnExpectedTemplate(EmailTemplateName templateName){
        EmailTemplate template = service.resolve(templateName);

        Assertions.assertEquals(templateName + "_SUBJECT", template.getSubject());
        Assertions.assertEquals(templateName + "_BODY", template.getBody());
    }
}
