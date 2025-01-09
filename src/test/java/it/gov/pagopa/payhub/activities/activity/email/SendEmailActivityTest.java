package it.gov.pagopa.payhub.activities.activity.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.exception.email.InvalidEmailConfigurationException;
import it.gov.pagopa.payhub.activities.service.EmailSenderService;
import it.gov.pagopa.payhub.activities.util.faker.EmailDTOFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendEmailActivityTest {

    @Mock
    private EmailSenderService emailSenderServiceMock;

    private SendEmailActivity activity;

    @BeforeEach
    void init(){
        activity = new SendEmailActivityImpl(emailSenderServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(emailSenderServiceMock);
    }

    @Test
    void givenNoDestinationWhenSendThenInvalidEmailConfigurationException() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setTo(null);

        // When
        InvalidEmailConfigurationException exNullArray = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.send(emailDTO));
        emailDTO.setTo(new String[]{""});
        InvalidEmailConfigurationException exEmptyDestination = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.send(emailDTO));

        // Then
        Assertions.assertEquals("Cannot send an email without a recipient", exNullArray.getMessage());
        Assertions.assertEquals(exNullArray.getMessage(), exEmptyDestination.getMessage());
    }

    @Test
    void givenNoSubjectWhenSendThenInvalidEmailConfigurationException() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setMailSubject(null);

        // When
        InvalidEmailConfigurationException ex = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.send(emailDTO));

        // Then
        Assertions.assertEquals("Cannot send an email without a subject", ex.getMessage());
    }

    @Test
    void givenNoBodyWhenSendThenInvalidEmailConfigurationException() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        emailDTO.setHtmlText(null);

        // When
        InvalidEmailConfigurationException ex = Assertions.assertThrows(InvalidEmailConfigurationException.class, () -> activity.send(emailDTO));

        // Then
        Assertions.assertEquals("Cannot send an email without a body", ex.getMessage());
    }

    @Test
    void givenCompleteConfigurationWhenSendThenOk() {
        // Given
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();

        // When
        activity.send(emailDTO);

        // Then
        Mockito.verify(emailSenderServiceMock).send(emailDTO);
    }
}
