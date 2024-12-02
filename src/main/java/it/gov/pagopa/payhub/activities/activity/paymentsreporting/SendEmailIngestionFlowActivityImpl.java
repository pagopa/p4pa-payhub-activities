package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.SendMailService;
import it.gov.pagopa.payhub.activities.dto.MailDTO;
import it.gov.pagopa.payhub.activities.helper.MailParameterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Implementation of SendEmailIngestionFlowActivity for send email ingestion flow activity.
 * Sends an email based on the status of a processed file identified by its IngestionFlow ID.
 */
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {
    private final SendMailService sendMailService;
    private final JavaMailSender javaMailSender;
    private final MailDTO mailDTO;

    public SendEmailIngestionFlowActivityImpl(SendMailService sendMailService, JavaMailSender javaMailSender, MailDTO mailDTO) {
        this.sendMailService = sendMailService;
        this.javaMailSender = javaMailSender;
        this.mailDTO =  mailDTO;
    }

    /**
     * Sends an email based on the process result of the given file ingestionFlow ID.
     *
     * @param ingestionFlowId       the unique identifier of the IngestionFlow record related to the imported file.
     * @param success      true if the process succeeded, false otherwise.
     * @return true if the email was sent successfully, false otherwise.
     */
    @Override
    public boolean sendEmail(String ingestionFlowId, boolean success) throws Exception {
        MailDTO mailToSendDTO = MailParameterHelper.getMailParameters(mailDTO);
        sendMailService.sendMail(javaMailSender, mailToSendDTO);
        return true;
    }

}