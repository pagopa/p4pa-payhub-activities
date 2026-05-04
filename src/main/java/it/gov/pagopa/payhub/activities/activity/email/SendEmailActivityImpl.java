package it.gov.pagopa.payhub.activities.activity.email;

import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.dto.email.*;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.payhub.activities.service.email.EmailSenderService;
import it.gov.pagopa.payhub.activities.service.email.EmailTemplateResolverService;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Lazy
@Service
public class SendEmailActivityImpl implements SendEmailActivity {

    private final EmailTemplateResolverService templateResolverService;
    private final EmailSenderService emailSenderService;
    private final BrokerService brokerService;

    public SendEmailActivityImpl(EmailTemplateResolverService templateResolverService, EmailSenderService emailSenderService, BrokerService brokerService) {
        this.templateResolverService = templateResolverService;
        this.emailSenderService = emailSenderService;
        this.brokerService = brokerService;
    }

    @Override
    public void sendTemplatedEmail(Long brokerId, TemplatedEmailDTO templatedEmail) {
        Broker broker = brokerService.getBrokerById(brokerId);
        EmailTemplate template = templateResolverService.resolve(broker.getExternalId(), templatedEmail.getTemplateName());

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(templatedEmail.getTo());
        emailDTO.setCc(templatedEmail.getCc());

        emailDTO.setMailSubject(resolvePlaceholders(template.getSubject(), templatedEmail.getParams()));
        String mailBody = resolvePlaceholders(template.getBody(), templatedEmail.getParams());
        emailDTO.setHtmlText(mailBody);
        emailDTO.setAttachments(templatedEmail.getAttachments());
        if (template.getInlines() != null) {
            emailDTO.setInlines(
                    template.getInlines()
                            .stream()
                            .map(this::mapToFileResource)
                            .toList()
            );
        }

        sendEmail(emailDTO, broker.getBrokerId());
    }

    private FileResourceDTO mapToFileResource(SerializableFileResourceDTO serializableFileResourceDTO) {
        return new FileResourceDTO(
                new ByteArrayResource(serializableFileResourceDTO.getFileContent()),
                serializableFileResourceDTO.getFileName()
        );
    }

    private static String resolvePlaceholders(String text, Map<String, String> params) {
        return StringSubstitutor.replace(text, params, "$[", "]");
    }

    @Override
    public void sendEmail(EmailDTO email, Long brokerId) {
        log.info("Sending email");
        validate(email);
        emailSenderService.send(email, brokerId);
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
