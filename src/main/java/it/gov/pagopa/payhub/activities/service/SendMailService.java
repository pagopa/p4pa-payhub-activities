package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class SendMailService {

    private final JavaMailSender javaMailSender;

    public SendMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     *
     * @param mailTo bean containing data to send
     * throws MessagingException if message is not sent
     */
    public void sendMail(MailTo mailTo) throws MessagingException {
        String subject = mailTo.getMailSubject();
        String htmlContent = mailTo.getHtmlText();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailTo.getEmailFromAddress());
        helper.setTo(mailTo.getTo());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }
}