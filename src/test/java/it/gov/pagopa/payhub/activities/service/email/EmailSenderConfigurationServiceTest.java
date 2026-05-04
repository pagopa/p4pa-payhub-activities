package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.connector.organization.BrokerConfigurationService;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfig;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import uk.co.jemos.podam.api.PodamFactory;

import java.lang.reflect.Field;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class EmailSenderConfigurationServiceTest {
    private final PodamFactory  podamFactory = TestUtils.getPodamFactory();
    private static final Long BROKER_ID = 1L;
    private static final String BROKER_EXTERNAL_ID = "brokerExternalId";
    private static final String ENV_PREFIX = BROKER_EXTERNAL_ID.toUpperCase() + "_";
    private Map<String, String> environmentVariables;

    @Mock
    private BrokerConfigurationService brokerConfigurationServiceMock;
    @Mock
    private BrokerService brokerServiceMock;

    private EmailSenderConfigurationService service;

    @BeforeEach
    void init() throws Exception {
        service = new EmailSenderConfigurationService(brokerConfigurationServiceMock, brokerServiceMock);

        Class<?> classOfMap = System.getenv().getClass();
        Field field = classOfMap.getDeclaredField("m");
        field.setAccessible(true);
        environmentVariables = (Map<String, String>)field.get(System.getenv());
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(brokerConfigurationServiceMock, brokerServiceMock);
    }

    @AfterEach
    void resetEnvironmentVariables() {
        resetEnvVars();
    }

    @Test
    void givenMailServerConfigWhenGetMailSenderThenBuildFromMailServerConfig() {
        String mailSenderAddress = "mailSenderAddress";
        EmailServerConfig expectedMailConfig = podamFactory.manufacturePojo(EmailServerConfig.class);

        EmailServerConfigDTO emailServerConfigDTO = new EmailServerConfigDTO();
        emailServerConfigDTO.setMailServerConfig(expectedMailConfig);
        emailServerConfigDTO.setMailSenderAddress(mailSenderAddress);

        Mockito.when(brokerConfigurationServiceMock.getBrokerEmailServerConfig(BROKER_ID))
                .thenReturn(emailServerConfigDTO);

        Pair<String, JavaMailSender> result = service.getMailSender(BROKER_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mailSenderAddress, result.getLeft());
        validateMailSender(expectedMailConfig, (JavaMailSenderImpl) result.getRight());
    }

    @Test
    void givenNoMailServerConfigWhenGetMailSenderThenBuildFromEnv() {
        EmailServerConfigDTO emailServerConfigDTO = new EmailServerConfigDTO();
        emailServerConfigDTO.setMailServerConfig(null);
        emailServerConfigDTO.setBrokerExternalId(BROKER_EXTERNAL_ID);
        EmailServerConfig expectedMailConfig = podamFactory.manufacturePojo(EmailServerConfig.class);
        String expectedEmailSenderAddress = "mailSenderAddress";

        Mockito.when(brokerConfigurationServiceMock.getBrokerEmailServerConfig(BROKER_ID))
                .thenReturn(emailServerConfigDTO);

        stubEnvVars( expectedMailConfig, expectedEmailSenderAddress);

        Pair<String, JavaMailSender> result = service.getMailSender(BROKER_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedEmailSenderAddress, result.getLeft());
        validateMailSender(expectedMailConfig, (JavaMailSenderImpl) result.getRight());
    }

    @Test
    void givenNullEmailServerConfigWhenGetMailSenderThenBuildFromEnv() {
        EmailServerConfig expectedMailConfig = podamFactory.manufacturePojo(EmailServerConfig.class);
        String expectedEmailSenderAddress = "mailSenderAddress";
        Mockito.when(brokerConfigurationServiceMock.getBrokerEmailServerConfig(BROKER_ID))
                .thenReturn(null);

        Broker broker = new Broker();
        broker.setExternalId(BROKER_EXTERNAL_ID);
        Mockito.when(brokerServiceMock.getBrokerById(BROKER_ID)).thenReturn(broker);

        stubEnvVars( expectedMailConfig, expectedEmailSenderAddress);

        Pair<String, JavaMailSender> result = service.getMailSender(BROKER_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedEmailSenderAddress, result.getLeft());
        validateMailSender(expectedMailConfig, (JavaMailSenderImpl) result.getRight());
    }

    @Test
    void givenNullEmailServerConfigAndMissingEnvVarsWhenGetMailSenderThenThrowInvalidEmailConfigurationException() {
        Mockito.when(brokerConfigurationServiceMock.getBrokerEmailServerConfig(BROKER_ID))
                .thenReturn(null);

        Broker broker = new Broker();
        broker.setExternalId(BROKER_EXTERNAL_ID);
        Mockito.when(brokerServiceMock.getBrokerById(BROKER_ID)).thenReturn(broker);

        Assertions.assertThrows(InvalidEmailConfigurationException.class,
                () -> service.getMailSender(BROKER_ID));
    }

    @Test
    void givenNullEmailServerConfigAndInvalidPortEnvVarWhenGetMailSenderThenThrowInvalidEmailConfigurationException() {
        EmailServerConfig expectedMailConfig = podamFactory.manufacturePojo(EmailServerConfig.class);

        Mockito.when(brokerConfigurationServiceMock.getBrokerEmailServerConfig(BROKER_ID))
                .thenReturn(null);

        Broker broker = new Broker();
        broker.setExternalId(BROKER_EXTERNAL_ID);
        Mockito.when(brokerServiceMock.getBrokerById(BROKER_ID)).thenReturn(broker);

        stubEnvVars(expectedMailConfig, "");
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_PORT, "NAN");

        Assertions.assertThrows(InvalidEmailConfigurationException.class,
                () -> service.getMailSender(BROKER_ID));
    }

    private void stubEnvVars(EmailServerConfig mailServerConfig, String mailSenderAddress) {
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_HOST, mailServerConfig.getHost());
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_PORT, String.valueOf(mailServerConfig.getPort()));
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_USERNAME, mailServerConfig.getUsername());
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_PASSWORD, mailServerConfig.getPassword());
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_SMTP_STARTTLS, String.valueOf(mailServerConfig.getStartTls()));
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_SMTP_STARTTLS_REQUIRED, String.valueOf(mailServerConfig.getStartTlsRequired()));
        environmentVariables.put(ENV_PREFIX + EmailSenderConfigurationService.MAIL_SENDER_ADDRESS, mailSenderAddress);
    }

    private void resetEnvVars() {
        environmentVariables.remove(ENV_PREFIX + EmailSenderConfigurationService.MAIL_HOST);
        environmentVariables.remove(ENV_PREFIX + EmailSenderConfigurationService.MAIL_PORT);
        environmentVariables.remove(ENV_PREFIX + EmailSenderConfigurationService.MAIL_USERNAME);
        environmentVariables.remove(ENV_PREFIX + EmailSenderConfigurationService.MAIL_PASSWORD);
        environmentVariables.remove(ENV_PREFIX + EmailSenderConfigurationService.MAIL_SMTP_STARTTLS);
        environmentVariables.remove(ENV_PREFIX + EmailSenderConfigurationService.MAIL_SMTP_STARTTLS_REQUIRED);
        environmentVariables.remove(ENV_PREFIX + EmailSenderConfigurationService.MAIL_SENDER_ADDRESS);
    }

    private void validateMailSender(EmailServerConfig expectedMailConfig, JavaMailSenderImpl mailSender) {
        Assertions.assertEquals(expectedMailConfig.getHost(), mailSender.getHost());
        Assertions.assertEquals(expectedMailConfig.getPort(), mailSender.getPort());
        Assertions.assertEquals(expectedMailConfig.getUsername(), mailSender.getUsername());
        Assertions.assertEquals(expectedMailConfig.getPassword(), mailSender.getPassword());
        Assertions.assertEquals(expectedMailConfig.getStartTls(), mailSender.getJavaMailProperties().get("mail.smtp.starttls.enable"));
        Assertions.assertEquals(expectedMailConfig.getStartTlsRequired(), mailSender.getJavaMailProperties().get("mail.smtp.starttls.required"));
    }
}