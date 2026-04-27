package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.util.faker.EmailDTOFaker;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    private static final String DEFAULT_FROM_ADDRESS = "FROMADDRESS";

    @Mock
    private JavaMailSender javaMailSenderMock;

    private EmailSenderService service;

    @BeforeEach
    void init() {
        service = new EmailSenderService(DEFAULT_FROM_ADDRESS, javaMailSenderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(javaMailSenderMock);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "test_sender@mailtest.com"})
    void whenSendThenOk(String senderEmail) throws MessagingException, IOException {
        // Given
        String expectedSenderEmail = senderEmail.isEmpty() ? null : senderEmail;
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO(expectedSenderEmail);

        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};
        capturePreparedEmail(result);

        // When
        service.send(emailDTO, null);

        // Then
        checkResultMessage(result[0], emailDTO);
        Assertions.assertEquals(emailDTO.getHtmlText(), ((MimeMultipart)((MimeMultipart)result[0].getContent()).getBodyPart(0).getContent()).getBodyPart(0).getContent());
    }

    @Test
    void whenSendWithAttachmentThenOk() throws MessagingException, IOException {
        // Given
        Path workingDirectory = Path.of("build", "test");
        Files.createDirectories(workingDirectory);

        String fileContent = "This is a test attachment content.";
        String fileName = "receipt.txt";

        byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);

        Resource testResource = new ByteArrayResource(fileBytes);

        FileResourceDTO expectedAttachment = FileResourceDTO.builder()
            .fileName(fileName)
            .resource(testResource)
            .build();

        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setAttachment(expectedAttachment);

        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};
        capturePreparedEmail(result);

        // When
        service.send(emailDTO, null);

        // Then
        MimeMessage resultMessage = result[0];
        checkResultMessage(resultMessage, emailDTO);

        MimeMultipart mainMultipart = (MimeMultipart) resultMessage.getContent();
        Assertions.assertEquals(2, mainMultipart.getCount(), "Expected 2 parts: text content and attachment.");
        Assertions.assertEquals(emailDTO.getHtmlText(), ((MimeMultipart) (mainMultipart).getBodyPart(0).getContent()).getBodyPart(0).getContent());

        BodyPart attachmentPart = mainMultipart.getBodyPart(1);
        Assertions.assertEquals(fileName, attachmentPart.getFileName());

        String actualAttachmentContent = new String(
            attachmentPart.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );
        Assertions.assertEquals(fileContent, actualAttachmentContent);
    }

    @Test
    void givenThrowExceptionWhenAddInlinesThenContinue() throws MessagingException {
        // Given
        List<FileResourceDTO> inlines = new ArrayList<>();
        FileResourceDTO inline = Mockito.mock(FileResourceDTO.class);
        Mockito.when(inline.getFileName()).thenReturn("filename.txt");
        Mockito.when(inline.getResource()).thenReturn(new ByteArrayResource(new byte[0]));
        inlines.add(inline);

        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        MimeMessageHelper mimeMessageHelperMock = Mockito.mock(MimeMessageHelper.class);
        String errorMessage = "error";
        Mockito.doThrow(new MessagingException(errorMessage)).when(mimeMessageHelperMock)
                .addInline(Mockito.anyString(), Mockito.any(Resource.class));

        //When - Then
        Assertions.assertDoesNotThrow(() -> service.addInlines(emailDTO, inlines, mimeMessageHelperMock));
    }

    private void capturePreparedEmail(MimeMessage[] result) {
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
    }

    @Test
    void whenSendWithInlinesThenOk() throws MessagingException, IOException {
        // Given
        Path workingDirectory = Path.of("build", "test");
        Files.createDirectories(workingDirectory);

        List<FileResourceDTO> inlines = new ArrayList<>();
        String expectedInlineContent = "dummy file content";
        String expectedInlineName = "filename.txt";
        FileResourceDTO inline = new FileResourceDTO(new ByteArrayResource(expectedInlineContent.getBytes()), expectedInlineName);
        inlines.add(inline);

        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();

        MimeMessage[] result = new MimeMessage[]{new MimeMessage((Session) null)};

        capturePreparedEmail(result);

        // When
        service.send(emailDTO, inlines);

        // Then
        MimeMessage resultMessage = result[0];
        checkResultMessage(resultMessage, emailDTO);

        MimeMultipart mainMultipart = (MimeMultipart) resultMessage.getContent();
        Assertions.assertEquals(1, mainMultipart.getCount(), "Expected 1 parts: text content.");
        Assertions.assertEquals(emailDTO.getHtmlText(), ((MimeMultipart) (mainMultipart).getBodyPart(0).getContent()).getBodyPart(0).getContent());

        checkInline(expectedInlineContent, expectedInlineName, mainMultipart);
    }

    private static void checkInline(String expectedInlineContent, String expectedInlineName, MimeMultipart mainMultipart) throws MessagingException, IOException {
        ByteArrayInputStream is = (ByteArrayInputStream)((MimeMultipart) (mainMultipart).getBodyPart(0).getContent()).getBodyPart("<"+expectedInlineName+">").getContent();
        int isLength = is.available();
        byte[] buffer = new byte[isLength];
        is.read(buffer, 0, isLength);
        String actualInlineContent = new String(buffer, StandardCharsets.UTF_8);
        Assertions.assertEquals(expectedInlineContent, actualInlineContent);
    }

    private static void checkResultMessage(MimeMessage resultMessage, EmailDTO emailDTO) throws MessagingException {
        Assertions.assertEquals(1, resultMessage.getHeader("From").length);
        Assertions.assertEquals(1, resultMessage.getHeader("To").length);
        Assertions.assertEquals(1, resultMessage.getHeader("CC").length);
        Assertions.assertEquals(1, resultMessage.getHeader("Subject").length);

        Assertions.assertEquals(emailDTO.getFrom()==null ? DEFAULT_FROM_ADDRESS : emailDTO.getFrom(), resultMessage.getHeader("From")[0]);
        Assertions.assertEquals(emailDTO.getTo()[0], resultMessage.getHeader("To")[0]);
        Assertions.assertEquals(emailDTO.getCc()[0], resultMessage.getHeader("CC")[0]);
        Assertions.assertEquals(emailDTO.getMailSubject(), resultMessage.getHeader("Subject")[0]);
    }
}
