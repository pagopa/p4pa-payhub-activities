package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class EmailSenderService {

    private final EmailSenderConfigurationService emailSenderConfigurationService;

    public EmailSenderService(EmailSenderConfigurationService emailSenderConfigurationService) {
        this.emailSenderConfigurationService = emailSenderConfigurationService;
    }

    /**
     * sending mail with JavaMailSender
     *
     * @param emailDTO bean containing data to send
     */
    public void send(EmailDTO emailDTO) {
        Pair<String, JavaMailSender> mailSender = emailSenderConfigurationService.getMailSender(emailDTO.getBrokerId());
        mailSender.getRight().send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(mailSender.getLeft());
            message.setTo(emailDTO.getTo());
            if (ArrayUtils.isNotEmpty(emailDTO.getCc())){
                message.setCc(emailDTO.getCc());
            }
            message.setSubject(emailDTO.getMailSubject());
            message.setText(emailDTO.getHtmlText(), true);
            if (emailDTO.getAttachments() != null) {
                addAttachments(message, emailDTO.getAttachments());
            }
            if(emailDTO.getInlines() != null) {
                addInlines(message, emailDTO.getInlines());
            }
            log.debug("sending mail message");
        });
    }

    void addAttachments(MimeMessageHelper message, List<FileResourceDTO> attachments) {
        attachments.forEach(i -> {
            try {
                message.addAttachment(i.getFileName(), i.getResource());
            } catch (MessagingException e) {
                log.error("Error in loading attachment with filename {}: {}", i.getFileName(), e.getMessage());
            }
        });
    }

    void addInlines(MimeMessageHelper message, List<FileResourceDTO> inlines) {
        inlines.forEach(i -> {
            try {
                message.addInline(i.getFileName(), i.getFileName(), i.getResource());
            } catch (Exception e) {
                log.error("Error in loading inline with CID {}: {}", i.getFileName(), e.getMessage());
            }
        });
    }

}