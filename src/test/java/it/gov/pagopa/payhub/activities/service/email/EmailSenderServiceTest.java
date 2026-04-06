package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.util.faker.EmailDTOFaker;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    private static final String FROM_ADDRESS = "FROMADDRESS";

    @Mock
    private JavaMailSender javaMailSenderMock;

    private EmailSenderService service;

    @BeforeEach
    void init() {
        service = new EmailSenderService(FROM_ADDRESS, javaMailSenderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(javaMailSenderMock);
    }

    @Test
    void givenPuEmailWhenSendThenOk() throws MessagingException, IOException {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};

        Mockito.doNothing()
                .when(javaMailSenderMock)
                .send(Mockito.<MimeMessagePreparator>argThat(m -> {
                    try {
                        m.prepare(result[0]);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }

                    return true;
                }));

        // When
        service.send(emailDTO);

        // Then
        MimeMessage resultMessage = result[0];
        checkResultMessage(resultMessage, emailDTO);

        MimeMultipart mainMultipart = (MimeMultipart) resultMessage.getContent();
        MimeMultipart emailBodyMultipart = (MimeMultipart) mainMultipart.getBodyPart(0).getContent();
        Assertions.assertEquals(emailDTO.getHtmlText(), emailBodyMultipart.getBodyPart(0).getContent());
    }

    @Test
    void givenCieEmailWhenSendThenOk() throws MessagingException, IOException {
        // Given
        EmailSenderService spiedService = Mockito.spy(service);

        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setCieEmail(true);
        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};

        Mockito.doNothing()
                .when(javaMailSenderMock)
                .send(Mockito.<MimeMessagePreparator>argThat(m -> {
                    try {
                        m.prepare(result[0]);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }

                    return true;
                }));

        ClassLoader mockedClassloader = Mockito.mock(ClassLoader.class);
        InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.readAllBytes())
                        .thenReturn(new byte[0]);

        Mockito.when(mockedClassloader.getResourceAsStream(Mockito.anyString()))
                .thenReturn(mockedInputStream);
        Mockito.when(spiedService.getClassLoader())
                .thenReturn(mockedClassloader);

        // When
        spiedService.send(emailDTO);

        // Then
        MimeMessage resultMessage = result[0];
        checkResultMessage(resultMessage, emailDTO);

        MimeMultipart mainMultipart = (MimeMultipart) resultMessage.getContent();
        MimeMultipart emailBodyMultipart = (MimeMultipart) mainMultipart.getBodyPart(0).getContent();
        Assertions.assertEquals(emailDTO.getHtmlText(), emailBodyMultipart.getBodyPart(0).getContent());

        BodyPart logoInline = emailBodyMultipart.getBodyPart("<logo-cie>");
        Assertions.assertNotNull(logoInline);
    }

    @Test
    void givenCieEmailWithNonExistingLogoResourceWhenSendThenSendWithNoLogo() throws MessagingException, IOException {
        // Given
        EmailSenderService spiedService = Mockito.spy(service);

        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setCieEmail(true);
        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};

        Mockito.doNothing()
                .when(javaMailSenderMock)
                .send(Mockito.<MimeMessagePreparator>argThat(m -> {
                    try {
                        m.prepare(result[0]);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }

                    return true;
                }));

        ClassLoader mockedClassloader = Mockito.mock(ClassLoader.class);

        Mockito.when(mockedClassloader.getResourceAsStream(Mockito.anyString()))
                .thenReturn(null);
        Mockito.when(spiedService.getClassLoader())
                .thenReturn(mockedClassloader);

        // When
        spiedService.send(emailDTO);

        // Then
        MimeMessage resultMessage = result[0];
        checkResultMessage(resultMessage, emailDTO);

        MimeMultipart mainMultipart = (MimeMultipart) resultMessage.getContent();
        MimeMultipart emailBodyMultipart = (MimeMultipart) mainMultipart.getBodyPart(0).getContent();
        Assertions.assertEquals(emailDTO.getHtmlText(), emailBodyMultipart.getBodyPart(0).getContent());

        BodyPart logoInline = emailBodyMultipart.getBodyPart("<logo-cie>");
        Assertions.assertNull(logoInline);
    }

    @Test
    void givenCieEmailWithErrorInLoadingLogoResourceWhenSendThenSendWithNoLogo() throws MessagingException, IOException {
        // Given
        EmailSenderService spiedService = Mockito.spy(service);

        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setCieEmail(true);
        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};

        Mockito.doNothing()
                .when(javaMailSenderMock)
                .send(Mockito.<MimeMessagePreparator>argThat(m -> {
                    try {
                        m.prepare(result[0]);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }

                    return true;
                }));

        ClassLoader mockedClassloader = Mockito.mock(ClassLoader.class);
        InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.readAllBytes())
                .thenReturn(null);

        Mockito.when(mockedClassloader.getResourceAsStream(Mockito.anyString()))
                .thenReturn(mockedInputStream);
        Mockito.when(spiedService.getClassLoader())
                .thenReturn(mockedClassloader);

        // When
        spiedService.send(emailDTO);

        // Then
        MimeMessage resultMessage = result[0];
        checkResultMessage(resultMessage, emailDTO);

        MimeMultipart mainMultipart = (MimeMultipart) resultMessage.getContent();
        MimeMultipart emailBodyMultipart = (MimeMultipart) mainMultipart.getBodyPart(0).getContent();
        Assertions.assertEquals(emailDTO.getHtmlText(), emailBodyMultipart.getBodyPart(0).getContent());

        BodyPart logoInline = emailBodyMultipart.getBodyPart("<logo-cie>");
        Assertions.assertNull(logoInline);
    }

    @Test
    void givenPuEmailWithAttachmentWhenSendThenOk() throws MessagingException, IOException {
        // Given
        FileResourceDTO expectedAttachment = prepareAttachment();

        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setAttachment(expectedAttachment);

        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};

        Mockito.doNothing()
            .when(javaMailSenderMock)
            .send(Mockito.<MimeMessagePreparator>argThat(m -> {
                try {
                    m.prepare(result[0]);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }

                return true;
            }));

        // When
        service.send(emailDTO);

        // Then
        MimeMessage resultMessage = result[0];
        checkResultMessage(resultMessage, emailDTO);

        MimeMultipart mainMultipart = (MimeMultipart) resultMessage.getContent();
        Assertions.assertEquals(2, mainMultipart.getCount(), "Expected 2 parts: text content and attachment.");
        Assertions.assertEquals(emailDTO.getHtmlText(), ((MimeMultipart) (mainMultipart).getBodyPart(0).getContent()).getBodyPart(0).getContent());

        BodyPart attachmentPart = mainMultipart.getBodyPart(1);
        Assertions.assertEquals(expectedAttachment.getFileName(), attachmentPart.getFileName());

        String actualAttachmentContent = new String(
            attachmentPart.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );
        Assertions.assertEquals(expectedAttachment.getResource().getContentAsString(StandardCharsets.UTF_8), actualAttachmentContent);
    }

    private FileResourceDTO prepareAttachment() throws IOException {
        Path workingDirectory = Path.of("build", "test");
        Files.createDirectories(workingDirectory);

        String fileContent = "This is a test attachment content.";
        String fileName = "receipt.txt";

        byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);

        Resource testResource = new ByteArrayResource(fileBytes);

        return FileResourceDTO.builder()
                .fileName(fileName)
                .resource(testResource)
                .build();
    }

    private static void checkResultMessage(MimeMessage resultMessage, EmailDTO emailDTO) throws MessagingException {
        Assertions.assertEquals(1, resultMessage.getHeader("From").length);
        Assertions.assertEquals(1, resultMessage.getHeader("To").length);
        Assertions.assertEquals(1, resultMessage.getHeader("CC").length);
        Assertions.assertEquals(1, resultMessage.getHeader("Subject").length);

        Assertions.assertEquals(FROM_ADDRESS, resultMessage.getHeader("From")[0]);
        Assertions.assertEquals(emailDTO.getTo()[0], resultMessage.getHeader("To")[0]);
        Assertions.assertEquals(emailDTO.getCc()[0], resultMessage.getHeader("CC")[0]);
        Assertions.assertEquals(emailDTO.getMailSubject(), resultMessage.getHeader("Subject")[0]);
    }
}
