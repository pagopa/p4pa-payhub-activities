package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.dto.MailDTO;
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
     * @param mailDTO  bean containing data to send
     * @throws Exception exception in case of send mail problems
     */

    public void sendMail(JavaMailSender javaMailSender, MailDTO mailDTO) throws Exception {
        String subject = mailDTO.getMailSubject();
        String htmlContent = mailDTO.getHtmlText();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailDTO.getEmailFromAddress());
        helper.setTo(mailDTO.getTo());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Sending mail
        javaMailSender.send(message);

        log.info("MAIL has been send");
    }
}