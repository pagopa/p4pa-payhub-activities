package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.connector.organization.BrokerConfigurationService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfig;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Lazy
@Service
@Slf4j
public class EmailSenderConfigurationService {
    public static final String MAIL_HOST = "MAIL_HOST";
    public static final String MAIL_PORT = "MAIL_PORT";
    public static final String MAIL_USERNAME = "MAIL_USERNAME";
    public static final String MAIL_PASSWORD = "MAIL_PASSWORD";
    public static final String MAIL_SMTP_STARTTLS = "MAIL_SMTP_STARTTLS";
    public static final String MAIL_SMTP_STARTTLS_REQUIRED = "MAIL_SMTP_STARTTLS_REQUIRED";
    public static final String MAIL_SENDER_ADDRESS = "MAIL_SENDER_ADDRESS";
    private final Map<String, Pair<String,JavaMailSender>> mailSenderMap;

    private final BrokerConfigurationService brokerConfigurationService;
    private final BrokerService brokerService;

    public EmailSenderConfigurationService(BrokerConfigurationService brokerConfigurationService, BrokerService brokerService){
        this.brokerConfigurationService = brokerConfigurationService;
        this.brokerService = brokerService;
        mailSenderMap = new ConcurrentHashMap<>();
    }

    /**
     * Returns the sender's email address and the email sender ({@link JavaMailSender}) associated with the specified broker
     *
     * @param brokerId the brokerId of the broker
     * @return a {@link Pair} containing:
     *         <ul>
     *           <li>the sender email address</li>
     *           <li>the configured {@link JavaMailSender}</li>
     *         </ul>
     * @throws InvalidEmailConfigurationException if the environment variable configuration
     *         is incomplete or contains invalid values
     */
    public Pair<String, JavaMailSender> getMailSender(Long brokerId) {
        EmailServerConfigDTO emailServerConfigDTO = brokerConfigurationService.getBrokerEmailServerConfig(brokerId);
        if(emailServerConfigDTO != null && emailServerConfigDTO.getMailServerConfig()!=null){
            EmailServerConfig mailServerConfig = emailServerConfigDTO.getMailServerConfig();
            JavaMailSender mailSender = buildJavaMailSender(
                    mailServerConfig.getHost(),
                    mailServerConfig.getPort(),
                    mailServerConfig.getUsername(),
                    mailServerConfig.getPassword(),
                    mailServerConfig.getStartTls(),
                    mailServerConfig.getStartTlsRequired()
            );
            return Pair.of(emailServerConfigDTO.getMailSenderAddress(), mailSender);
        }
        String brokerExternalId = emailServerConfigDTO != null
                ? emailServerConfigDTO.getBrokerExternalId()
                : brokerService.getBrokerById(brokerId).getExternalId();
        return getMailSenderFromEnv(brokerExternalId);


    }

    private Pair<String, JavaMailSender> getMailSenderFromEnv(String externalBrokerId) {
        return mailSenderMap.computeIfAbsent(externalBrokerId, extId -> {
            String host = System.getenv(extId.toUpperCase() + "_" + MAIL_HOST);
            String portStr = System.getenv(extId.toUpperCase() + "_" + MAIL_PORT);
            String username = System.getenv(extId.toUpperCase() + "_" + MAIL_USERNAME);
            String password = System.getenv(extId.toUpperCase() + "_" + MAIL_PASSWORD);
            String starttls = System.getenv(extId.toUpperCase() + "_" + MAIL_SMTP_STARTTLS);
            String starttlsRequired = System.getenv(extId.toUpperCase() + "_" + MAIL_SMTP_STARTTLS_REQUIRED);
            String senderAddress = System.getenv(extId.toUpperCase() + "_" + MAIL_SENDER_ADDRESS);
            if (host == null || portStr == null || username == null || password == null ||
                    starttls == null || starttlsRequired == null || senderAddress == null) {
                log.error("Missing mail configuration for broker {}", extId);
                throw new InvalidEmailConfigurationException("Missing mail configuration for broker " + extId);
            }

            int port;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                log.error("Invalid port for broker {}: {}", extId, portStr);
                throw new InvalidEmailConfigurationException("Invalid port for broker " + extId);
            }

            return Pair.of(senderAddress, buildJavaMailSender(host, port, username, password, Boolean.parseBoolean(starttls), Boolean.parseBoolean(starttlsRequired)));
        });
    }

    private JavaMailSender buildJavaMailSender(String host, Integer port, String username, String password, Boolean starttls, Boolean starttlsRequired) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.smtp.starttls.required", starttlsRequired);
        return mailSender;
    }
}
