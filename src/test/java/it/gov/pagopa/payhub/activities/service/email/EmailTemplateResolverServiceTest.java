package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = EmailTemplatesConfiguration.class)
class EmailTemplateResolverServiceTest {

    @Autowired
    private EmailTemplatesConfiguration emailTemplatesConfiguration;

    private EmailTemplateResolverService service;

    @BeforeEach
    void init(){
        service = new EmailTemplateResolverService(emailTemplatesConfiguration);
    }

    @Test
    void testEmailTemplatesConfigurationNotNull(){
        TestUtils.checkNotNullFields(emailTemplatesConfiguration);
    }

    @ParameterizedTest
    @EnumSource(EmailTemplateName.class)
    void givenEnumWhenResolveThenReturnExpectedTemplate(EmailTemplateName templateName) {
        EmailTemplate template = service.resolve(templateName);

        assertPropertyValue(template.getSubject());
        assertPropertyValue(template.getBody());
    }

    private void assertPropertyValue(String value){
        Assertions.assertNotNull(value);
        Assertions.assertFalse(value.contains("${"), "Template not correctly resolved! " + value);
    }
}
