package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.service.email.facade.EmailTemplateFacadeService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@EnableConfigurationProperties(value = EmailTemplatesConfiguration.class)
class EmailTemplateResolverServiceTest {

    public static final String BROKER_EXTERNAL_ID = "BROKER_EXTERNAL_ID";
    @Autowired
    private EmailTemplatesConfiguration emailTemplatesConfiguration;
    @Mock
    private EmailTemplateFacadeService emailTemplateFacadeService;

    private EmailTemplateResolverService service;

    @BeforeEach
    void init(){
        service = new EmailTemplateResolverService(emailTemplatesConfiguration, emailTemplateFacadeService);
    }

    @Test
    void testEmailTemplatesConfigurationNotNull(){
        TestUtils.checkNotNullFields(emailTemplatesConfiguration);
    }

    @ParameterizedTest
    @EnumSource(EmailTemplateName.class)
    void givenEnumWhenResolveThenReturnExpectedTemplate(EmailTemplateName templateName) {
        EmailTemplate template = service.resolve(BROKER_EXTERNAL_ID, templateName);

        assertPropertyValue(template.getSubject());
        assertPropertyValue(template.getBody());
    }

    private void assertPropertyValue(String value){
        Assertions.assertNotNull(value);
        Assertions.assertFalse(value.contains("${"), "Template not correctly resolved! " + value);
    }

    @Test
    void givenFoundTemplateOnRepositoryWhenResolveThenReturnExpectedTemplate() {
        //GIVEN
        EmailTemplate expectedEmailTemplate = new EmailTemplate("REPO_SUBJECT", "REPO_BODY", null);
        Mockito.when(emailTemplateFacadeService.fetchTemplate(BROKER_EXTERNAL_ID, EmailTemplateName.INGESTION_PAYMENT_NOTIFICATION_OK, "Pagamenti notificati: Resoconto Caricamento file: $[fileName]"))
                .thenReturn(expectedEmailTemplate);

        //WHEN
        EmailTemplate template = service.resolve(BROKER_EXTERNAL_ID, EmailTemplateName.INGESTION_PAYMENT_NOTIFICATION_OK);

        //THEN
        Assertions.assertEquals(expectedEmailTemplate.getSubject(), template.getSubject());
        Assertions.assertEquals(expectedEmailTemplate.getBody(), template.getBody());
    }
}
