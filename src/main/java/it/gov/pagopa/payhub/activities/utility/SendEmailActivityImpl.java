package it.gov.pagopa.payhub.activities.utility;

import it.gov.pagopa.payhub.activities.config.EmailConfig;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of SendEmailActivity for sending email
 */
@Slf4j
@Service
public class SendEmailActivityImpl implements SendEmailActivity {
    /**
     * Sends an email
     *
     * @param sendMailService  service to send e-mail
     * @param javaMailSender   java mail sender
     * @param mailTo          bean containing mail data
     * @throws Exception       exception if the sending of mal fails
     */
    public boolean sendEmail(SendMailService sendMailService, JavaMailSender javaMailSender, MailTo mailTo) throws Exception {
        return sendMailService.sendMail(javaMailSender, mailTo);
    }

}