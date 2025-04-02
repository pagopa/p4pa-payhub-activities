package it.gov.pagopa.payhub.activities.activity.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.payhub.activities.service.email.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class SendEmailActivityImpl implements SendEmailActivity {

    private final EmailSenderService emailSenderService;

    public SendEmailActivityImpl(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void sendEmail(EmailDTO email) {
        log.info("Sending email");
        validate(email);
        emailSenderService.send(email);
    }

    private void validate(EmailDTO email) {
        if(ArrayUtils.isEmpty(email.getTo()) || StringUtils.isEmpty(email.getTo()[0])){
            throw new InvalidEmailConfigurationException("Cannot send an email without a recipient");
        }
        if(StringUtils.isEmpty(email.getMailSubject())){
            throw new InvalidEmailConfigurationException("Cannot send an email without a subject");
        }
        if(StringUtils.isEmpty(email.getHtmlText())){
            throw new InvalidEmailConfigurationException("Cannot send an email without a body");
        }
    }

    @Override
    public void sendTemplatedEmail(TemplatedEmailDTO email) {

    }
}
