package it.gov.pagopa.payhub.activities.activity.email;

import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.dto.email.*;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.payhub.activities.service.email.EmailSenderService;
import it.gov.pagopa.payhub.activities.service.email.EmailTemplateResolverService;
import it.gov.pagopa.payhub.activities.util.faker.EmailDTOFaker;
import it.gov.pagopa.payhub.activities.util.faker.TemplatedEmailDTOFaker;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class SendEmailActivityTest {

    @Mock
    private EmailTemplateResolverService templateResolverServiceMock;
    @Mock
    private EmailSenderService emailSenderServiceMock;
    @Mock
    private BrokerService brokerServiceMock;

    private SendEmailActivity activity;

    @BeforeEach
    void init(){
        activity = new SendEmailActivityImpl(templateResolverServiceMock, emailSenderServiceMock, brokerServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(templateResolverServiceMock, emailSenderServiceMock, brokerServiceMock);
    }

    @Test
    void givenNoDestinationWhenSendEmailThenInvalidEmailConfigurationException() {
        // Given
        long brokerId = 1L;
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setTo(null);

        // When
        InvalidEmailConfigurationException exNullArray = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO, brokerId));
        emailDTO.setTo(new String[]{""});
        InvalidEmailConfigurationException exEmptyDestination = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO, brokerId));

        // Then
        Assertions.assertEquals("Cannot send an email without a recipient", exNullArray.getMessage());
        Assertions.assertEquals(exNullArray.getMessage(), exEmptyDestination.getMessage());
    }

    @Test
    void givenNoSubjectWhenSendEmailThenInvalidEmailConfigurationException() {
        // Given
        Long brokerId = 1L;
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setMailSubject(null);

        // When
        InvalidEmailConfigurationException ex = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO, brokerId));

        // Then
        Assertions.assertEquals("Cannot send an email without a subject", ex.getMessage());
    }

    @Test
    void givenNoBodyWhenSendEmailThenInvalidEmailConfigurationException() {
        // Given
        Long brokerId = 1L;
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setHtmlText(null);

        // When
        InvalidEmailConfigurationException ex = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.sendEmail(emailDTO, brokerId));

        // Then
        Assertions.assertEquals("Cannot send an email without a body", ex.getMessage());
    }

    @Test
    void givenCompleteConfigurationWhenSendEmailThenOk() {
        // Given
        Long brokerId = 1L;
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();

        Mockito.doNothing().when(emailSenderServiceMock).send(emailDTO,brokerId);

        // When
        Assertions.assertDoesNotThrow(()->activity.sendEmail(emailDTO, brokerId));
    }

    @Test
    void givenNoInlinesWhenSendTemplatedEmailThenOk(){
        // Given
        Long brokerId = 1L;
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        Map<String, String> params = Map.of(
                "var1", "VALUE1",
                "var2", "VALUE2",
                "var3", "VALUE3",
                "var4", "VALUE4",
                "var5", "VALUE5"
        );
        FileResourceDTO attachment = new FileResourceDTO(
                new ByteArrayResource("PDF-DATA".getBytes()),
                "filename"
        );
        TemplatedEmailDTO templatedEmailDTO = TemplatedEmailDTOFaker.buildTemplatedEmailDTO(params);
        templatedEmailDTO.setAttachments(List.of(attachment));

        EmailTemplate template = new EmailTemplate("SUBJECT $[var1] $[var2]", "BODY $[var3] $[var4]", null);
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getBrokerId()).thenReturn(brokerId);
        Mockito.when(broker.getExternalId()).thenReturn(brokerExternalId);
        Mockito.when(brokerServiceMock.getBrokerById(brokerId))
                .thenReturn(broker);
        Mockito.when(templateResolverServiceMock.resolve(brokerExternalId, templatedEmailDTO.getTemplateName()))
                .thenReturn(template);

        // When
        activity.sendTemplatedEmail(brokerId, templatedEmailDTO);

        // Then
        Mockito.verify(emailSenderServiceMock).send(Mockito.argThat(e -> {
            Assertions.assertSame(templatedEmailDTO.getTo(), e.getTo());
            Assertions.assertSame(templatedEmailDTO.getCc(), e.getCc());
            Assertions.assertSame(templatedEmailDTO.getAttachments(), e.getAttachments());
            Assertions.assertEquals("SUBJECT VALUE1 VALUE2", e.getMailSubject());
            Assertions.assertEquals("BODY VALUE3 VALUE4", e.getHtmlText());
            Assertions.assertNull(e.getInlines());
            return true;
        }), Mockito.eq(brokerId));
    }

    @Test
    void whenSendTemplatedEmailThenOk(){
        // Given
        Long brokerId = 1L;
        String brokerExternalId = "BROKER_EXTERNAL_ID";
        Map<String, String> params = Map.of(
                "var1", "VALUE1",
                "var2", "VALUE2",
                "var3", "VALUE3",
                "var4", "VALUE4",
                "var5", "VALUE5"
        );
        FileResourceDTO attachment = new FileResourceDTO(
                new ByteArrayResource("PDF-DATA".getBytes()),
                "filename"
        );
        TemplatedEmailDTO templatedEmailDTO = TemplatedEmailDTOFaker.buildTemplatedEmailDTO(params);
        templatedEmailDTO.setAttachments(List.of(attachment));

        String inlineFileContent = "INLINE-DATA";
        SerializableFileResourceDTO inline = new SerializableFileResourceDTO(
                inlineFileContent.getBytes(),
                "inline_filename"
        );
        FileResourceDTO expectedInline = new FileResourceDTO(
                new ByteArrayResource(inlineFileContent.getBytes()),
                "inline_filename"
        );
        EmailTemplate template = new EmailTemplate("SUBJECT $[var1] $[var2]", "BODY $[var3] $[var4]", List.of(inline));
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getExternalId()).thenReturn(brokerExternalId);
        Mockito.when(broker.getBrokerId()).thenReturn(brokerId);
        Mockito.when(brokerServiceMock.getBrokerById(brokerId))
                        .thenReturn(broker);
        Mockito.when(templateResolverServiceMock.resolve(brokerExternalId, templatedEmailDTO.getTemplateName()))
                .thenReturn(template);

        // When
        activity.sendTemplatedEmail(brokerId, templatedEmailDTO);

        // Then
        Mockito.verify(emailSenderServiceMock).send(Mockito.argThat(e -> {
            Assertions.assertSame(templatedEmailDTO.getTo(), e.getTo());
            Assertions.assertSame(templatedEmailDTO.getCc(), e.getCc());
            Assertions.assertSame(templatedEmailDTO.getAttachments(), e.getAttachments());
            Assertions.assertEquals("SUBJECT VALUE1 VALUE2", e.getMailSubject());
            Assertions.assertEquals("BODY VALUE3 VALUE4", e.getHtmlText());
            Assertions.assertEquals(List.of(expectedInline), e.getInlines());
            return true;
        }),Mockito.eq(brokerId));
    }
}
