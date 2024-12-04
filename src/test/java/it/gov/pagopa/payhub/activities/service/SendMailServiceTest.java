package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.exception.SendMailException;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.PreencodedMimeBodyPart;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest(
		classes = {SendEmailIngestionFlowActivityImpl.class},
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
		"template.OK.body=mail body",
		"template.OK.subject=mail subject",
})
@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {
	@MockBean
	private SendMailService sendMailService ;
	private JavaMailSenderImpl javaMailSender;
	private MailTo validMailOk;
	private MailTo invalidMailOk;
	private MailTo validMailKo;
	private MailTo invalidMailKo;

	@BeforeEach
	void setup() throws MessagingException {
		createBeans();
		javaMailSender = new JavaMailSenderImpl();
		sendMailService  = new SendMailService(javaMailSender);
	}

	@Test
	void testSendEmailFileLoadedSuccess() throws MessagingException {
		Assertions.assertDoesNotThrow(() -> sendMailService.sendMail(validMailOk));
		Mockito.verify(sendMailService, times(1)).sendMail(validMailOk);
	}

	@Test
	void testSendEmailFileNotLoadedSuccess() throws MessagingException {
		Assertions.assertDoesNotThrow(() -> sendMailService.sendMail(validMailKo));
		Mockito.verify(sendMailService, times(1)).sendMail(validMailKo);
	}

/*
	@Test
	void testSendEmailFileLoadedFailed() throws MessagingException {
		assertThrows(MessagingException.class, () ->
		sendMailService.sendMail(invalidMailOk), "Error while sending mail");
	}

	@Test
	void testSendEmailFileNotLoadedFailed() {
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidMailKo), "Error while sending mail");
	}
*/

	private void createBeans() {
		validMailOk = MailTo.builder()
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.emailFromAddress("test_sender@mailtest.com")
				.templateName("reportingFlow-ok")
				.build();
		validMailKo = MailTo.builder()
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.emailFromAddress("test_sender@mailtest.com")
				.templateName("reportingFlow-ko")
				.build();
		invalidMailOk = MailTo.builder()
				.mailSubject(null)
				.to(new String[]{})
				.templateName("reportingFlow-ok")
				.build();
		invalidMailKo = MailTo.builder()
				.mailSubject(null)
				.to(new String[]{})
				.templateName("reportingFlow-ko")
				.build();
	}

}
