package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class EmailSenderService {

    private final String senderMailAddress;
    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    public EmailSenderService(
            @Value("${mail.sender-address:}") String senderMailAddress,
            JavaMailSender mailSender,
            ResourceLoader resourceLoader
    ) {
        this.senderMailAddress = senderMailAddress;
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
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
            if(emailDTO.isCieEmail()) {
                Resource resource = resourceLoader.getResource("classpath:CIE-logo.svg");
                if(resource.exists()) {
                    try {
                        byte[] logoBytes = resource.getContentAsByteArray();
                        message.addInline("logo-cie", new ByteArrayResource(logoBytes), "image/svg+xml");
                    } catch (Exception e) {
                        log.warn("Error during loading of CIE logo: {}", e.getMessage());
                    }
                } else {
                    log.warn("Error in finding CIE logo in classpath");
                }
			}
            log.debug("sending mail message");
        });
    }

    public static void main(String[] args) {
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource("classpath:CIE/logo/CIE-logo.svg");
        System.out.println(resource.exists());
    }

}