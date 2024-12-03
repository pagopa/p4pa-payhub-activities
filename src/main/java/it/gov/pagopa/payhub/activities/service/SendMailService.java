package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendMailService {
    /**
     *
     * @param javaMailSender mail sender
     * @param mailTo bean containing data to send
     */
    public void sendMail(JavaMailSender javaMailSender, MailTo mailTo) throws Exception {
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