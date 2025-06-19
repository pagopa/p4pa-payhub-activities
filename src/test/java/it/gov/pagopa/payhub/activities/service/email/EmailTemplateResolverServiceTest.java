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
            "exportMailTextOk",
            "exportMailTextKo",

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_PAYMENTS_REPORTING_OK_SUBJECT", "INGESTION_PAYMENTS_REPORTING_OK_BODY"),
                    new EmailTemplate("INGESTION_PAYMENTS_REPORTING_KO_SUBJECT", "INGESTION_PAYMENTS_REPORTING_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_PAYMENT_NOTIFICATION_OK_SUBJECT", "INGESTION_PAYMENT_NOTIFICATION_OK_BODY"),
                    new EmailTemplate("INGESTION_PAYMENT_NOTIFICATION_KO_SUBJECT", "INGESTION_PAYMENT_NOTIFICATION_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_TREASURY_OPI_OK_SUBJECT", "INGESTION_TREASURY_OPI_OK_BODY"),
                    new EmailTemplate("INGESTION_TREASURY_OPI_KO_SUBJECT", "INGESTION_TREASURY_OPI_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_TREASURY_POSTE_OK_SUBJECT", "INGESTION_TREASURY_POSTE_OK_BODY"),
                    new EmailTemplate("INGESTION_TREASURY_POSTE_KO_SUBJECT", "INGESTION_TREASURY_POSTE_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_TREASURY_XLS_OK_SUBJECT", "INGESTION_TREASURY_XLS_OK_BODY"),
                    new EmailTemplate("INGESTION_TREASURY_XLS_KO_SUBJECT", "INGESTION_TREASURY_XLS_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_TREASURY_CSV_OK_SUBJECT", "INGESTION_TREASURY_CSV_OK_BODY"),
                    new EmailTemplate("INGESTION_TREASURY_CSV_KO_SUBJECT", "INGESTION_TREASURY_CSV_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_TREASURY_CSV_COMPLETE_OK_SUBJECT", "INGESTION_TREASURY_CSV_COMPLETE_OK_BODY"),
                    new EmailTemplate("INGESTION_TREASURY_CSV_COMPLETE_KO_SUBJECT", "INGESTION_TREASURY_CSV_COMPLETE_KO_BODY")
            ),

            new EmailTemplate("INGESTION_PAGOPA_RT_SUBJECT", "INGESTION_PAGOPA_RT_BODY"),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_DP_INSTALLMENTS_OK_SUBJECT", "INGESTION_DP_INSTALLMENTS_OK_BODY"),
                    new EmailTemplate("INGESTION_DP_INSTALLMENTS_KO_SUBJECT", "INGESTION_DP_INSTALLMENTS_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_ORGANIZATIONS_OK_SUBJECT", "INGESTION_ORGANIZATIONS_OK_BODY"),
                    new EmailTemplate("INGESTION_ORGANIZATIONS_KO_SUBJECT", "INGESTION_ORGANIZATIONS_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_ORGANIZATIONS_SIL_SERVICE_OK_SUBJECT", "INGESTION_ORGANIZATIONS_SIL_SERVICE_OK_BODY"),
                    new EmailTemplate("INGESTION_ORGANIZATIONS_SIL_SERVICE_KO_SUBJECT", "INGESTION_ORGANIZATIONS_SIL_SERVICE_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_DEBT_POSITIONS_TYPE_OK_SUBJECT", "INGESTION_DEBT_POSITIONS_TYPE_OK_BODY"),
                    new EmailTemplate("INGESTION_DEBT_POSITIONS_TYPE_KO_SUBJECT", "INGESTION_DEBT_POSITIONS_TYPE_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_DEBT_POSITIONS_TYPE_ORG_OK_SUBJECT", "INGESTION_DEBT_POSITIONS_TYPE_ORG_OK_BODY"),
                    new EmailTemplate("INGESTION_DEBT_POSITIONS_TYPE_ORG_KO_SUBJECT", "INGESTION_DEBT_POSITIONS_TYPE_ORG_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_DEBT_POSITIONS_TYPE_ORG_OPERATORS_OK_SUBJECT", "INGESTION_DEBT_POSITIONS_TYPE_ORG_OPERATORS_OK_BODY"),
                    new EmailTemplate("INGESTION_DEBT_POSITIONS_TYPE_ORG_OPERATORS_KO_SUBJECT", "INGESTION_DEBT_POSITIONS_TYPE_ORG_OPERATORS_KO_BODY")
            ),

            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_ASSESSMENTS_OK_SUBJECT", "INGESTION_ASSESSMENTS_OK_BODY"),
                    new EmailTemplate("INGESTION_ASSESSMENTS_KO_SUBJECT", "INGESTION_ASSESSMENTS_KO_BODY")
            ),
            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_ASSESSMENTS_REGISTRY_OK_SUBJECT", "INGESTION_ASSESSMENTS_REGISTRY_OK_BODY"),
                    new EmailTemplate("INGESTION_ASSESSMENTS_REGISTRY_KO_SUBJECT", "INGESTION_ASSESSMENTS_REGISTRY_KO_BODY")
            ),
            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("INGESTION_RECEIPT_OK_SUBJECT", "INGESTION_RECEIPT_OK_BODY"),
                    new EmailTemplate("INGESTION_RECEIPT_KO_SUBJECT", "INGESTION_RECEIPT_KO_BODY")
            ),
            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("EXPORT_PAID_OK_SUBJECT", "EXPORT_PAID_OK_BODY"),
                    new EmailTemplate("EXPORT_PAID_KO_SUBJECT", "EXPORT_PAID_KO_BODY")
            ),
            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("EXPORT_RECEIPTS_ARCHIVING_OK_SUBJECT", "EXPORT_RECEIPTS_ARCHIVING_OK_BODY"),
                    new EmailTemplate("EXPORT_RECEIPTS_ARCHIVING_KO_SUBJECT", "EXPORT_RECEIPTS_ARCHIVING_KO_BODY")
            ),
            new EmailTemplatesConfiguration.EmailOutcomeBasedTemplates(
                    new EmailTemplate("EXPORT_CLASSIFICATIONS_OK_SUBJECT", "EXPORT_CLASSIFICATIONS_OK_BODY"),
                    new EmailTemplate("EXPORT_CLASSIFICATIONS_KO_SUBJECT", "EXPORT_CLASSIFICATIONS_KO_BODY")
            )
    );

    private final EmailTemplateResolverService service = new EmailTemplateResolverService(configMock);

    @ParameterizedTest
    @EnumSource(EmailTemplateName.class)
    void givenEnumWhenResolveThenReturnExpectedTemplate(EmailTemplateName templateName) {
        EmailTemplate template = service.resolve(templateName);

        Assertions.assertEquals(templateName + "_SUBJECT", template.getSubject());
        Assertions.assertEquals(templateName + "_BODY", template.getBody());
    }
}
