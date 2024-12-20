package it.gov.pagopa.payhub.activities.service;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Lazy
@Service
@Slf4j
public class SendMailService {
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String smtpAuth;
    private final String smtpStarttlsEnable;
    private final String smtpStarttlsRequired;
    private final JavaMailSenderImpl mailSender;

    public SendMailService(
            @Value("${activity.mail.host}") String host,
            @Value("${activity.mail.port}") String port,
            @Value("${activity.mail.username}") String username,
            @Value("${activity.mail.password}") String password,
            @Value("${activity.mail.properties.mail.smtp.auth}") String smtpAuth,
            @Value("${activity.mail.properties.mail.smtp.starttls.enable}") String smtpStarttlsEnable,
            @Value("${activity.mail.properties.mail.smtp.starttls.required}") String smtpStarttlsRequired,
            JavaMailSenderImpl mailSender
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.smtpAuth = smtpAuth;
        this.smtpStarttlsEnable = smtpStarttlsEnable;
        this.smtpStarttlsRequired = smtpStarttlsRequired;
        this.mailSender = mailSender;
    }

    /**
     * sending mail with JavaMailSender
     * @param mailTo bean containing data to send
     * @return true if the mail is sent, otherwise Exception
     * @throws MessagingException if message is not sent
     */
    public boolean sendMail(MailTo mailTo) throws MessagingException {
        try  {
            wrongData(mailTo);
            setMailSender();
            mailSender.send( mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(mailTo.getEmailFromAddress());
                message.setTo(mailTo.getTo());
                if(ArrayUtils.isNotEmpty(mailTo.getCc()))
                    message.setCc(mailTo.getCc());
                message.setSubject(mailTo.getMailSubject());
                message.setText(mailTo.getHtmlText(), true);
                log.debug("sending mail message");
            } );
        } catch (Exception e) {
            log.error("Mail not sent: {}", e.getMessage());
            throw e;
        }
        return true;
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

    private void setMailSender() {
        if (StringUtils.isNotEmpty(host))
            mailSender.setHost(host);
        if (StringUtils.isNotEmpty(port))
            mailSender.setPort(Integer.parseInt(port));
        if (StringUtils.isNotEmpty(username))
            mailSender.setUsername(username);
        if (StringUtils.isNotEmpty(password))
            mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        if (StringUtils.isNotEmpty(smtpAuth))
            props.put("mail.smtp.auth", smtpAuth);
        if (StringUtils.isNotEmpty(smtpStarttlsEnable))
            props.put("mail.smtp.starttls.enable", smtpStarttlsEnable);
        if (StringUtils.isNotEmpty(smtpStarttlsRequired))
            props.put("mail.smtp.starttls.required", smtpStarttlsRequired);
    }
}