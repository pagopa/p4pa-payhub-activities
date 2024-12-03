package it.gov.pagopa.payhub.activities.utility;

import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Interface for sending mails
 */
@Service
public interface SendEmailActivity {
    boolean sendEmail(SendMailService sendMailService, JavaMailSender javaMailSender, MailTo mailTo) throws Exception;
}
