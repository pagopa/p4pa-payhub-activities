package it.gov.pagopa.payhub.activities.service;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class SendMailService {
    private final JavaMailSender mailSender;

    public SendMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * sending mail with JavaMailSender
     * @param mailTo bean containing data to send
     * throws MessagingException if message is not sent
     */
    public void sendMail(MailTo mailTo) throws MessagingException {
        wrongData(mailTo);
        mailSender.send( mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(mailTo.getEmailFromAddress());
            message.setTo(mailTo.getTo());
            message.setSubject(mailTo.getMailSubject());
            message.setText(mailTo.getHtmlText(), true);
            log.debug("sending mail message.");
        } );
    }

    /**
     *
     * @param mailTo mail data
     * throws MessagingException in data are not valid
     */
    private void wrongData(MailTo mailTo) throws MessagingException {
        String except = "Mail data null or not valued";
        if (mailTo==null)
            throw new MessagingException(except);
        if (StringUtils.isBlank(mailTo.getEmailFromAddress()))
            throw new MessagingException(except);
        if (StringUtils.isBlank(mailTo.getMailSubject()))
            throw new MessagingException(except);
        if (StringUtils.isBlank(mailTo.getHtmlText()))
            throw new MessagingException(except);
        if (mailTo.getTo().length == 0)
            throw new MessagingException(except);
    }

}