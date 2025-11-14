package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.AttachmentDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.util.faker.EmailDTOFaker;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void whenSendThenOk() throws MessagingException, IOException {
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
        checkResultMessage(result[0], emailDTO);
        Assertions.assertEquals(emailDTO.getHtmlText(), ((MimeMultipart)((MimeMultipart)result[0].getContent()).getBodyPart(0).getContent()).getBodyPart(0).getContent());
    }

    @Test
    void whenSendWithAttachmentThenOk() throws MessagingException, IOException {
        // Given
        File testFile = File.createTempFile("test_attachment", ".txt");
        String fileContent = "This is a test attachment content.";
        java.nio.file.Files.writeString(testFile.toPath(), fileContent);

        AttachmentDTO expectedAttachment = AttachmentDTO.builder()
            .fileName("receipt.txt")
            .file(testFile)
            .build();

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
        Assertions.assertEquals(emailDTO.getHtmlText(), ((MimeMultipart)(mainMultipart).getBodyPart(0).getContent()).getBodyPart(0).getContent());

        BodyPart attachmentPart = mainMultipart.getBodyPart(1);
        Assertions.assertEquals(expectedAttachment.getFileName(), attachmentPart.getFileName());
        String actualAttachmentContent = new String(
            attachmentPart.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );
        Assertions.assertEquals(fileContent, actualAttachmentContent);

        testFile.delete();
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
