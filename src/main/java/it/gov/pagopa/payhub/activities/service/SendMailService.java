package it.gov.pagopa.payhub.activities.service;

import io.micrometer.common.util.StringUtils;
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
        if (wrongData(mailTo))
            throw new MessagingException("Mail data null or not valued");

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

    /**
     *
     * @param mailTo mail data
     * @return boolean false if data are wrong
     */
    private boolean wrongData(MailTo mailTo) {
        if (mailTo==null)
            return false;
        if (StringUtils.isBlank(mailTo.getEmailFromAddress()))
            return false;
        if (StringUtils.isBlank(mailTo.getMailSubject()))
            return false;
        if (StringUtils.isBlank(mailTo.getHtmlText()))
            return false;
        return mailTo.getTo().length != 0;
    }

}