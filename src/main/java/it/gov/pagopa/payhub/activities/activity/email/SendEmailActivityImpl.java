package it.gov.pagopa.payhub.activities.activity.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.payhub.activities.service.email.EmailSenderService;
import it.gov.pagopa.payhub.activities.service.email.EmailTemplateResolverService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Lazy
@Service
public class SendEmailActivityImpl implements SendEmailActivity {

    private final EmailTemplateResolverService templateResolverService;
    private final EmailSenderService emailSenderService;

    public SendEmailActivityImpl(EmailTemplateResolverService templateResolverService, EmailSenderService emailSenderService) {
        this.templateResolverService = templateResolverService;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void sendTemplatedEmail(TemplatedEmailDTO templatedEmail) {
        EmailTemplate template = templateResolverService.resolve(templatedEmail.getTemplateName());

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(templatedEmail.getTo());
        emailDTO.setCc(templatedEmail.getCc());

        emailDTO.setMailSubject(resolvePlaceholders(template.getSubject(), templatedEmail.getParams()));
        String mailBody = resolvePlaceholders(template.getBody(), templatedEmail.getParams());
        emailDTO.setHtmlText(Jsoup.clean(mailBody, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false)));

        emailDTO.setAttachment(templatedEmail.getAttachment());

        sendEmail(emailDTO);
    }

    private static String resolvePlaceholders(String text, Map<String, String> params) {
        return StringSubstitutor.replace(text, params, "%", "%");
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
}
