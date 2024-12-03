package it.gov.pagopa.payhub.activities.config;

import it.gov.pagopa.payhub.activities.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties
public class EmailConfig {
    @Value("${activities-mail-host}:host")
    private String host;

    @Value("${activities-mail-user}:user")
    private String user;

    @Value("${activities-mail-password}:password")
    private String password;

    @Value("${activities-mail-port}:587")
    private String port;

    /**
     * java mail sender
     * @return java mail sender
     */

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Set up mail configuration
        mailSender.setHost(host);
        mailSender.setPort(port==null ? Constants.SMTP_PORT : Integer.parseInt(port));
        mailSender.setUsername(user);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", false);
        return mailSender;
    }
}