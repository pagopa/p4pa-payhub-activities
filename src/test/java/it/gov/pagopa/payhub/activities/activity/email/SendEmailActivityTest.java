package it.gov.pagopa.payhub.activities.activity.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.payhub.activities.service.email.EmailSenderService;
import it.gov.pagopa.payhub.activities.service.email.EmailTemplateResolverService;
import it.gov.pagopa.payhub.activities.util.faker.EmailDTOFaker;
import java.util.Map;

import it.gov.pagopa.payhub.activities.util.faker.TemplatedEmailDTOFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendEmailActivityTest {

    @Mock
    private EmailTemplateResolverService templateResolverServiceMock;
    @Mock
    private EmailSenderService emailSenderServiceMock;

    private SendEmailActivity activity;

    @BeforeEach
    void init(){
        activity = new SendEmailActivityImpl(templateResolverServiceMock, emailSenderServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(templateResolverServiceMock, emailSenderServiceMock);
    }

    @Test
    void givenNoDestinationWhenSendEmailThenInvalidEmailConfigurationException() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setTo(null);

        // When
        InvalidEmailConfigurationException exNullArray = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO));
        emailDTO.setTo(new String[]{""});
        InvalidEmailConfigurationException exEmptyDestination = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO));

        // Then
        Assertions.assertEquals("Cannot send an email without a recipient", exNullArray.getMessage());
        Assertions.assertEquals(exNullArray.getMessage(), exEmptyDestination.getMessage());
    }

    @Test
    void givenNoSubjectWhenSendEmailThenInvalidEmailConfigurationException() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setMailSubject(null);

        // When
        InvalidEmailConfigurationException ex = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO));

        // Then
        Assertions.assertEquals("Cannot send an email without a subject", ex.getMessage());
    }

    @Test
    void givenNoBodyWhenSendEmailThenInvalidEmailConfigurationException() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setHtmlText(null);

        // When
        InvalidEmailConfigurationException ex = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO));

        // Then
        Assertions.assertEquals("Cannot send an email without a body", ex.getMessage());
    }

    @Test
    void givenCompleteConfigurationWhenSendEmailThenOk() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();

        // When
        activity.sendEmail(emailDTO);

        // Then
        Mockito.verify(emailSenderServiceMock).send(emailDTO);
    }

    @Test
    void whenSendTemplatedEmailThenOk(){
        // Given
        EmailTemplateName templateName = EmailTemplateName.INGESTION_PAGOPA_RT;
        Long brokerId = 1L;
        Map<String, String> params = Map.of(
                "var1", "VALUE1",
                "var2", "VALUE2",
                "var3", "VALUE3",
                "var4", "VALUE4",
                "var5", "VALUE5"
        );
        TemplatedEmailDTO templatedEmailDTO = TemplatedEmailDTOFaker.buildTemplatedEmailDTO(templateName, params);

        EmailTemplate template = new EmailTemplate("SUBJECT $[var1] $[var2]", "BODY $[var3] $[var4]");
        Mockito.when(templateResolverServiceMock.resolve(brokerId, templateName))
                .thenReturn(template);

        // When
        activity.sendTemplatedEmail(brokerId, templatedEmailDTO);

        // Then
        Mockito.verify(emailSenderServiceMock).send(Mockito.argThat(e -> {
            Assertions.assertSame(templatedEmailDTO.getFrom(), e.getFrom());
            Assertions.assertSame(templatedEmailDTO.getTo(), e.getTo());
            Assertions.assertSame(templatedEmailDTO.getCc(), e.getCc());
            Assertions.assertSame(templatedEmailDTO.getAttachment(), e.getAttachment());
            Assertions.assertEquals("SUBJECT VALUE1 VALUE2", e.getMailSubject());
            Assertions.assertEquals("BODY VALUE3 VALUE4", e.getHtmlText());

            return true;
        }));
    }
}
