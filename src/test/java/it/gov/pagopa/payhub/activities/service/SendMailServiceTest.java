package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.MessagingException;
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
import org.springframework.test.context.TestPropertySource;

import java.rmi.UnexpectedException;

import static org.mockito.ArgumentMatchers.isA;
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

	private MailTo validMailOk;
	private MailTo validMailKo;
	private MailTo invalidMailOk;
	private MailTo invalidMailKo;


	@BeforeEach
	void setup() {
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

	@Test
	void testSendEmailValidMail() throws MessagingException {
		SendMailService sms = mock(SendMailService.class);
		doNothing().when(sms).sendMail(isA(MailTo.class));
		sms.sendMail(validMailOk);
		Mockito.verify(sms, times(1)).sendMail(validMailOk);
	}

	@Test
	void testSendEmailOkInvalidMail() {
		SendMailService sms = mock(SendMailService.class);
		try {
			doThrow(new MessagingException()).when(sms).sendMail(invalidMailOk);
			sms.sendMail(invalidMailOk);
		} catch (MessagingException e) {
			System.out.println("Exception in sending invalid mail");
		}
	}

}