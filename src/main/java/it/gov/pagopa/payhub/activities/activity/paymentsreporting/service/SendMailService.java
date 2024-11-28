package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.model.MailParams;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendMailService {

    public void sendMail(JavaMailSender javaMailSender, MailParams mailParams) throws Exception {
        try {
            String subject = mailParams.getMailSubject();
            String htmlContent = mailParams.getHtmlText();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailParams.getEmailFromAddress());
            helper.setTo(mailParams.getTo());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(message);

            log.info("MAIL has been send");
        }
        catch (Exception e) {
            log.info("MAIL error");
            throw new SendMailException("Error in mail sending");
        }
    }
}