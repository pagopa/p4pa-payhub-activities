package it.gov.pagopa.payhub.activities.activity.service;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.SendMailService;
import it.gov.pagopa.payhub.activities.config.EmailConfig;
import it.gov.pagopa.payhub.activities.dto.MailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {
    MailDTO mailDTO;
    SendMailService sendMailService;
    JavaMailSender javaMailSender;

    @BeforeEach
    void init() {
        EmailConfig emailConfig = new EmailConfig();
        javaMailSender = emailConfig.getJavaMailSender();
        sendMailService = new SendMailService();
        mailDTO = new MailDTO();
    }

    /**
     * test setting mail data and then sending them
     */
    @Test
    void sendMail() {
        boolean result = true;
        mailDTO.setMailSubject("Mail Subject");
        mailDTO.setMailText("Mail text");
        mailDTO.setHtmlText("HTML Text");
        mailDTO.setEmailFromAddress("mailfrom@test.com");
        mailDTO.setTo(new String[]{"mailto@test.com"});

        try {
            sendMailService.sendMail(javaMailSender, mailDTO);
        }
        catch (Exception e){
            result = false;
        }
        assertFalse(result);
    }

}
