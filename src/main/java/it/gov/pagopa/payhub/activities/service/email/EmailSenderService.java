package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class EmailSenderService {

    private final String senderMailAddress;
    private final JavaMailSender mailSender;

    public EmailSenderService(
            @Value("${mail.sender-address:}") String senderMailAddress,

            JavaMailSender mailSender
    ) {
        this.senderMailAddress = senderMailAddress;

        this.mailSender = mailSender;
    }

    /**
     * sending mail with JavaMailSender
     *
     * @param emailDTO bean containing data to send
     */
    public void send(EmailDTO emailDTO, List<FileResourceDTO> inlines) {
        mailSender.send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(emailDTO.getFrom() == null ? senderMailAddress : emailDTO.getFrom());
            message.setTo(emailDTO.getTo());
            if (ArrayUtils.isNotEmpty(emailDTO.getCc())){
                message.setCc(emailDTO.getCc());
            }
            message.setSubject(emailDTO.getMailSubject());
            message.setText(emailDTO.getHtmlText(), true);
            if (emailDTO.getAttachment() != null) {
                message.addAttachment(
                    emailDTO.getAttachment().getFileName(),
                    emailDTO.getAttachment().getResource());
            }
            if(inlines != null) {
                addInlines(emailDTO, inlines, message);
            }
            log.debug("sending mail message");
        });
    }

    void addInlines(EmailDTO emailDTO, List<FileResourceDTO> inlines, MimeMessageHelper message) {
        inlines.forEach(i -> {
            try {
                message.addInline(i.getFileName(), i.getResource());
            } catch (Exception e) {
                log.warn("Error in loading inline with CID {} for email {}", i.getFileName(), emailDTO.getMailSubject());
            }
        });
    }

}