package it.gov.pagopa.payhub.activities.activity.service;

import it.gov.pagopa.payhub.activities.config.EmailConfig;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {
    SendMailService sendMailService;

    @BeforeEach
    void init() {
        sendMailService = new SendMailService();
    }

    /**
     * test setting mail data and then sending them
     */
    @Test
    void sendMail() {
        boolean result;
        MailTo mailTo = new MailTo();
        mailTo.setMailSubject("Mail Subject");
        mailTo.setMailText("Mail text");
        mailTo.setHtmlText("HTML Text");
        mailTo.setEmailFromAddress("mailfrom@test.com");
        mailTo.setTo(new String[]{"mailto@test.com"});

        try {
            EmailConfig emailConfig = new EmailConfig();
            JavaMailSender javaMailSender = emailConfig.getJavaMailSender();
            result = sendMailService.sendMail(javaMailSender, mailTo);
        }
        catch (Exception e){
            result = false;
        }
        assertFalse(result);
    }

}
