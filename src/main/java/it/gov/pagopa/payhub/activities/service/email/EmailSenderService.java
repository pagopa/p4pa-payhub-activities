package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;

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
    public void send(EmailDTO emailDTO) {
        mailSender.send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(senderMailAddress);
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
			try (InputStream is = getClass().getResourceAsStream("/CIE-sorgente.png")) {
                if(is !=null) {
                    byte[] logoBytes = is.readAllBytes();
			        message.addInline("logo-cie", new ByteArrayResource(logoBytes), "image/jpeg");
                }
			}
            log.debug("sending mail message");
        });
    }

}