package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.utility.faker.EmailDTOFaker;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
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

import java.io.IOException;

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
        Assertions.assertEquals(1, result[0].getHeader("From").length);
        Assertions.assertEquals(1, result[0].getHeader("To").length);
        Assertions.assertEquals(1, result[0].getHeader("CC").length);
        Assertions.assertEquals(1, result[0].getHeader("Subject").length);

        Assertions.assertEquals(FROM_ADDRESS, result[0].getHeader("From")[0]);
        Assertions.assertEquals(emailDTO.getTo()[0], result[0].getHeader("To")[0]);
        Assertions.assertEquals(emailDTO.getCc()[0], result[0].getHeader("CC")[0]);
        Assertions.assertEquals(emailDTO.getMailSubject(), result[0].getHeader("Subject")[0]);
        Assertions.assertEquals(emailDTO.getHtmlText(), ((MimeMultipart)((MimeMultipart)result[0].getContent()).getBodyPart(0).getContent()).getBodyPart(0).getContent());
    }
}
