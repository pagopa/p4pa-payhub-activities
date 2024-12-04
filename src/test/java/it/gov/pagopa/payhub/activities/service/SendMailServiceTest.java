package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.MessagingException;
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
	//private MailTo invalidMailOk;
	//private MailTo invalidMailKo;


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
/*
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
 */
	}

    @Test
	void testSendEmailOkSuccess() {
		boolean ret = true;
		try {
			Mockito.doNothing().when(sendMailService).sendMail(validMailOk);
		}
		catch (MessagingException e) {
			ret = false;
		}
		Assertions.assertTrue(ret);
    }

    @Test
	void testSendEmailKoSuccess() {
		boolean ret = true;
		try {
			Mockito.doNothing().when(sendMailService).sendMail(validMailKo);
		}
		catch (MessagingException e) {
			ret = false;
		}
		Assertions.assertTrue(ret);
	}

	/*
	@Test
	void testSendEmailOkFailed() {
		boolean ret = true;
		try {
			Mockito.doNothing().when(sendMailService).sendMail(invalidMailOk);
		}
		catch (MessagingException e) {
			ret = false;
		}
		Assertions.assertFalse(ret);
	}

	@Test
	void testSendEmailKoFailed() {
		boolean ret = true;
		try {
			Mockito.doNothing().when(sendMailService).sendMail(invalidMailKo);
		}
		catch (MessagingException e) {
			ret = false;
		}
		Assertions.assertFalse(ret);
	}
	*/
}